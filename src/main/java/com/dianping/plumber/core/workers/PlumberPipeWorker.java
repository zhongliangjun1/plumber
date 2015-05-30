package com.dianping.plumber.core.workers;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.PlumberGlobals;
import com.dianping.plumber.core.PlumberPagelet;
import com.dianping.plumber.core.ResultReturnedFlag;
import com.dianping.plumber.core.ResultType;
import com.dianping.plumber.core.definitions.PlumberPipeDefinition;
import com.dianping.plumber.core.monitor.Monitor;
import com.dianping.plumber.core.monitor.MonitorEvent;
import com.dianping.plumber.exception.PlumberPipeTimeoutException;
import com.dianping.plumber.utils.TimeUtils;
import com.dianping.plumber.utils.ViewRenderUtils;

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
                             MonitorEvent monitorEvent, ResultReturnedFlag resultReturnedFlag) {
        super(definition, paramsFromRequest, paramsFromController);
        this.pipe = pipe;
        this.hasPriority = true;
        this.pipeRenderResultQueue = null;
        this.startTime = null;
        this.resultReturnedFlag = resultReturnedFlag;
        this.monitorEvent = monitorEvent;
    }

    @Override
    public void run() {
        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;
        try {

            ResultType resultType = pipe.execute(paramsFromRequest, paramsFromController,
                modelForView);
            if (resultType == ResultType.SUCCESS)
                renderResult = ViewRenderUtils.getViewRenderResult(definition, modelForView);

        } catch (Exception e) {

            renderResult = ViewRenderUtils.getViewRenderResultWhenExceptionHappen(e);
            logger.error("pipe " + definition.getName() + " execute failure", e);

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
            boolean result = pipeRenderResultQueue.offer(renderResult,
                TimeUtils.getRemainingTime(startTime, PlumberConfig.getResponseTimeout()),
                TimeUnit.MILLISECONDS);
            if (!result) {
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
