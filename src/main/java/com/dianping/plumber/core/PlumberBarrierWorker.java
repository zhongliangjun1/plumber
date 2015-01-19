package com.dianping.plumber.core;

import com.dianping.plumber.utils.StringUtils;
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
        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;

        try {

            ResultType resultType = barrier.execute(paramsFromRequest, paramsFromController, modelForView);

            if ( resultType==ResultType.SUCCESS ) {
                String viewSource = definition.getViewSource();
                ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
                String result = viewRenderer.render(name, viewSource, modelForView);
                if ( StringUtils.isNotEmpty(result) )
                    renderResult = result;
            }

        } catch (Exception e) {

            if ( isDevEnv() ) {
                String result = ExceptionUtils.getFullStackTrace(e);
                if ( StringUtils.isNotEmpty(result) )
                    renderResult = result;
            }

            String msg = "barrier " + name + " execute failure";
            logger.error(msg, e);

        } finally {

            try {
                barrierRenderResults.put(name, renderResult);
            } catch (Exception e) {
                logger.error("I don't know what would happen, but just to make sure latch.countDown() can be executed all the time ", e);
            }

            latch.countDown();

        }
    }

}
