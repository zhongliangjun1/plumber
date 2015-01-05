package com.dianping.plumber.core;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.view.ViewRenderer;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-11
 * Time: PM10:29
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPipeWorker extends PlumberWorker {

    private final PlumberPipe pipe;
    private final LinkedBlockingQueue<String> pipeRenderResultQueue;

    public PlumberPipeWorker(PlumberPipeDefinition definition,
                             Map<String, Object> paramsFromRequest,
                             Map<String, Object> paramsFromController,
                             PlumberPipe pipe,
                             LinkedBlockingQueue<String> pipeRenderResultQueue) {
        super(definition, paramsFromRequest, paramsFromController);
        this.pipe = pipe;
        this.pipeRenderResultQueue = pipeRenderResultQueue;
    }

    @Override
    public void run() {
        try {
            ResultType resultType = pipe.execute(paramsFromRequest, paramsFromController, modelForView);
            if ( resultType==ResultType.SUCCESS ) {
                String name = definition.getName();
                String viewSource = definition.getViewSource();
                ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
                String renderResult = viewRenderer.render(name, viewSource, modelForView);
                pipeRenderResultQueue.put(renderResult);
            } else {
                pipeRenderResultQueue.put(PlumberGlobals.EMPTY_RENDER_RESULT);
            }
        } catch (Exception e) {

            try {
                if ( isDevEnv() ) {
                    pipeRenderResultQueue.put(ExceptionUtils.getFullStackTrace(e));
                } else {
                    pipeRenderResultQueue.put(PlumberGlobals.EMPTY_RENDER_RESULT);
                }
            } catch (InterruptedException e1) {
                logger.error("terrible!", e1);
            }

            String msg = "pipe " + definition.getName() + " execute failure";
            logger.error(msg, e);

        }
    }

}
