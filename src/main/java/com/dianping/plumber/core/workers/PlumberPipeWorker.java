package com.dianping.plumber.core.workers;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.*;
import com.dianping.plumber.core.definitions.PlumberPipeDefinition;
import com.dianping.plumber.core.monitor.Monitor;
import com.dianping.plumber.core.monitor.MonitorEvent;
import com.dianping.plumber.exception.PlumberPipeTimeoutException;
import com.dianping.plumber.utils.EnvUtils;
import com.dianping.plumber.utils.StringUtils;
import com.dianping.plumber.utils.TimeUtils;
import com.dianping.plumber.view.ViewRenderer;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-11
 * Time: PM10:29
 */
public class PlumberPipeWorker extends PlumberWorker {

    private final PlumberPagelet              pipe;
    private final LinkedBlockingQueue<String> pipeRenderResultQueue;
    private final boolean                     hasPriority;
    private final Date                        startTime;
    private final ResultReturnedFlag          resultReturnedFlag;
    private final MonitorEvent                monitorEvent;

    public PlumberPipeWorker(PlumberPipeDefinition definition,
                             Map<String, Object> paramsFromRequest,
                             Map<String, Object> paramsFromController, PlumberPagelet pipe,
                             LinkedBlockingQueue<String> pipeRenderResultQueue, Date startTime,
                             ResultReturnedFlag resultReturnedFlag) {
        super(definition, paramsFromRequest, paramsFromController);
        this.pipe = pipe;
        this.hasPriority = false;
        this.pipeRenderResultQueue = pipeRenderResultQueue;
        this.startTime = startTime;
        this.resultReturnedFlag = resultReturnedFlag;
        this.monitorEvent = null;
    }

    public PlumberPipeWorker(PlumberPipeDefinition definition,
                             Map<String, Object> paramsFromRequest,
                             Map<String, Object> paramsFromController, PlumberPagelet pipe,
                             MonitorEvent monitorEvent) {
        super(definition, paramsFromRequest, paramsFromController);
        this.pipe = pipe;
        this.hasPriority = true;
        this.pipeRenderResultQueue = null;
        this.startTime = null;
        this.resultReturnedFlag = null;
        this.monitorEvent = monitorEvent;
    }

    @Override
    public void run() {
        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;
        try {
            ResultType resultType = pipe.execute(paramsFromRequest, paramsFromController,
                modelForView);
            if (resultType == ResultType.SUCCESS) {
                String name = definition.getName();
                String viewSource = definition.getViewSource();
                if (EnvUtils.isDev()) { // for refresh
                    String viewPath = definition.getViewPath();
                    viewSource = PlumberWorkerDefinitionsRepo.getViewSourceLoader().load(viewPath);
                }
                ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
                String result = viewRenderer.render(name, viewSource, modelForView);
                if (StringUtils.isNotEmpty(result))
                    renderResult = result;
            }
        } catch (Exception e) {

            logger.error("pipe " + definition.getName() + " execute failure", e);

            if (EnvUtils.isDev()) {
                try {
                    String result = ExceptionUtils.getFullStackTrace(e);
                    if (StringUtils.isNotEmpty(result))
                        renderResult = result;
                } catch (Exception e1) {
                    logger.error("can not get the exception full stack trace", e1);
                }
            }

        } finally {

            if (resultReturnedFlag.isReturned()) {
                logger.error(
                    "can not return the pipe " + definition.getName() + "'s render result",
                    new PlumberPipeTimeoutException());
                return;
            }

            if (!hasPriority) {
                sendBackRenderResult(renderResult);
            } else {
                signalMonitor(renderResult);
            }

        }
    }

    private void sendBackRenderResult(String renderResult) {
        try {
            boolean insertResult = pipeRenderResultQueue.offer(renderResult,
                TimeUtils.getRemainingTime(startTime, PlumberConfig.getResponseTimeout()),
                TimeUnit.MILLISECONDS);
            if (!insertResult) {
                logger.error(
                    "can not return the pipe " + definition.getName() + "'s render result",
                    new PlumberPipeTimeoutException());
            }
        } catch (InterruptedException e) {
            logger.error("can not return the pipe " + definition.getName() + "'s render result",
                new PlumberPipeTimeoutException(e));
        }
    }

    private void signalMonitor(String renderResult) {
        Monitor.signal(definition.getName(), renderResult, monitorEvent);
    }

}
