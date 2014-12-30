package com.dianping.plumber.core;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.view.ViewRenderer;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: AM12:26
 * To change this template use File | Settings | File Templates.
 */
public class PlumberBarrierWorker extends PlumberWorker {

    private final CountDownLatch latch;
    private final PlumberBarrier barrier;
    private final ConcurrentHashMap<String,String> barrierRenderResults;

    public PlumberBarrierWorker(PlumberBarrierDefinition definition,
                                Map<String, Object> paramsFromRequest,
                                Map<String, Object> paramsFromController,
                                CountDownLatch latch,
                                PlumberBarrier barrier,
                                ConcurrentHashMap<String, String> barrierRenderResults) {
        super(definition, paramsFromRequest, paramsFromController);
        this.latch = latch;
        this.barrier = barrier;
        this.barrierRenderResults = barrierRenderResults;
    }

    @Override
    public void run() {
        String name = definition.getName();
        try {
            ResultType resultType = barrier.execute(paramsFromRequest, paramsFromController, modelForView);
            if ( resultType==ResultType.SUCCESS ) {
                String viewSource = definition.getViewSource();
                ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
                String renderResult = viewRenderer.render(name, viewSource, modelForView);
                if ( renderResult!=null ) { // protect ConcurrentHashMap
                    barrierRenderResults.put(name, renderResult);
                } else {
                    barrierRenderResults.put(name, PlumberGlobals.EMPTY_RENDER_RESULT);
                }
            } else {
                barrierRenderResults.put(name, PlumberGlobals.EMPTY_RENDER_RESULT);
            }
        } catch (Exception e) {
            if ( isDevEnv() ) {
                barrierRenderResults.put(name, ExceptionUtils.getFullStackTrace(e));
            } else {
                barrierRenderResults.put(name, PlumberGlobals.EMPTY_RENDER_RESULT);
            }
            String msg = "barrier " + name + " execute failure";
            logger.error(msg, e);
        } finally {
            latch.countDown();
        }
    }

    private static boolean isDevEnv() {
        return PlumberGlobals.DEV_ENV.equals(PlumberConfig.getEnv());
    }

}
