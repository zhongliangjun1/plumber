package com.dianping.plumber.core;

import com.dianping.plumber.view.ViewRenderer;

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

    public PlumberBarrierWorker(PlumberBarrierDefinition definition, Map<String, Object> paramsFromController,
                                CountDownLatch latch, PlumberBarrier barrier,
                                ConcurrentHashMap<String, String> barrierRenderResults) {
        super(definition, paramsFromController);
        this.latch = latch;
        this.barrier = barrier;
        this.barrierRenderResults = barrierRenderResults;
    }

    @Override
    public void run() {
        try {
            ResultType resultType = barrier.execute(paramsFromController, modelForView);
            String name = definition.getName();
            if ( resultType==ResultType.SUCCESS ) {
                String viewSource = definition.getViewSource();
                ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
                String renderResult = viewRenderer.render(name, viewSource, modelForView);
                barrierRenderResults.put(name, renderResult);
            } else {
                barrierRenderResults.put(name, PlumberGlobals.EMPTY_RENDER_RESULT);
            }
        } catch (Exception e) {
            barrierRenderResults.put(definition.getName(), PlumberGlobals.EMPTY_RENDER_RESULT);
            logger.error(e);
        } finally {
            latch.countDown();
        }
    }
}
