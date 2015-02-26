package com.dianping.plumber.core.workers;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.*;
import com.dianping.plumber.core.definitions.PlumberPipeDefinition;
import com.dianping.plumber.core.workers.PlumberWorker;
import com.dianping.plumber.exception.PlumberPipeTimeoutException;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.view.ViewRenderer;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-11
 * Time: PM10:29
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPipeWorker extends PlumberWorker {

    private final PlumberPagelet pipe;
    private final LinkedBlockingQueue<String> pipeRenderResultQueue;
    private final Lock flushLock;
    private final Condition flushCondition;
    private final AtomicInteger currentPipeSeqLocation;
    private final ResultReturnedFlag resultReturnedFlag;

    public PlumberPipeWorker(PlumberPipeDefinition definition,
                             Map<String, Object> paramsFromRequest,
                             Map<String, Object> paramsFromController,
                             PlumberPagelet pipe,
                             Lock flushLock,
                             Condition flushCondition,
                             AtomicInteger currentPipeSeqLocation,
                             LinkedBlockingQueue<String> pipeRenderResultQueue,
                             ResultReturnedFlag resultReturnedFlag) {
        super(definition, paramsFromRequest, paramsFromController);
        this.pipe = pipe;
        this.flushLock = flushLock;
        this.flushCondition = flushCondition;
        this.currentPipeSeqLocation = currentPipeSeqLocation;
        this.pipeRenderResultQueue = pipeRenderResultQueue;
        this.resultReturnedFlag = resultReturnedFlag;
    }

    @Override
    public void run() {
        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;
        try {
            ResultType resultType = pipe.execute(paramsFromRequest, paramsFromController, modelForView);
            if ( resultType==ResultType.SUCCESS ) {
                String name = definition.getName();
                String viewSource = definition.getViewSource();
                ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
                String result = viewRenderer.render(name, viewSource, modelForView);
                if ( StringUtils.isNotEmpty(result) )
                    renderResult = result;
            }
        } catch (Exception e) {

            logger.error("pipe " + definition.getName() + " execute failure", e);

            if ( isDevEnv() ) {
                try {
                    String result = ExceptionUtils.getFullStackTrace(e);
                    if ( StringUtils.isNotEmpty(result) )
                        renderResult = result;
                } catch (Exception e1) {
                    logger.error("can not get the exception full stack trace", e1);
                }
            }

        } finally {

            if ( flushLock==null ) {

                sendBackRenderResult(renderResult);

            } else {

                PlumberPipeDefinition plumberPipeDefinition = (PlumberPipeDefinition) definition;
                Integer pipeSeqLocation = plumberPipeDefinition.getSeqLocation();

                flushLock.lock();
                try {
                    while ( pipeSeqLocation>=currentPipeSeqLocation.get() ) {
                        if ( pipeSeqLocation==currentPipeSeqLocation.get() ) {
                            sendBackRenderResult(renderResult);
                            currentPipeSeqLocation.incrementAndGet();
                            flushCondition.signalAll();
                        } else {
                            try {
                                flushCondition.await();
                            } catch (InterruptedException e) {
                                logger.error("can not return the pipe " + definition.getName() + "'s render result", e);
                                break;
                            }
                        }
                    }
                } finally {
                    flushLock.unlock();
                }

            }

        }
    }

    private void sendBackRenderResult(String renderResult) {

        if ( resultReturnedFlag.isReturned() ) {
            logger.error("can not return the pipe " + definition.getName() + "'s render result", new PlumberPipeTimeoutException());
            return;
        }

        try {
            boolean insertResult = pipeRenderResultQueue.offer(renderResult, PlumberConfig.getResponseTimeout(), TimeUnit.MILLISECONDS);
            if ( !insertResult ) {
                logger.error("can not return the pipe " + definition.getName() + "'s render result", new PlumberPipeTimeoutException());
            }
        } catch (InterruptedException e) {
            logger.error("can not return the pipe " + definition.getName() + "'s render result", new PlumberPipeTimeoutException(e));
        }

    }




}
