package com.dianping.plumber.core.workers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.dianping.plumber.core.PlumberGlobals;
import com.dianping.plumber.core.PlumberPagelet;
import com.dianping.plumber.core.ResultType;
import com.dianping.plumber.core.definitions.PlumberBarrierDefinition;
import com.dianping.plumber.utils.ViewRenderUtils;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: AM12:26
 * To change this template use File | Settings | File Templates.
 */
public class PlumberBarrierWorker extends PlumberWorker {

    private final CountDownLatch                    latch;
    private final PlumberPagelet                    barrier;
    private final ConcurrentHashMap<String, String> barrierRenderResults;

    public PlumberBarrierWorker(PlumberBarrierDefinition definition,
                                Map<String, Object> paramsFromRequest,
                                Map<String, Object> paramsFromController, CountDownLatch latch,
                                PlumberPagelet barrier,
                                ConcurrentHashMap<String, String> barrierRenderResults) {
        super(definition, paramsFromRequest, paramsFromController);
        this.latch = latch;
        this.barrier = barrier;
        this.barrierRenderResults = barrierRenderResults;
    }

    @Override
    public void run() {

        String renderResult = PlumberGlobals.EMPTY_RENDER_RESULT;
        String name = definition.getName();

        try {

            ResultType resultType = barrier.execute(paramsFromRequest, paramsFromController,
                modelForView);

            if (resultType == ResultType.SUCCESS)
                renderResult = ViewRenderUtils.getViewRenderResult(definition, modelForView);

        } catch (Exception e) {

            renderResult = ViewRenderUtils.getViewRenderResultWhenExceptionHappen(e);
            logger.error("barrier " + name + " execute failure", e);

        } finally {

            try {
                barrierRenderResults.put(name, renderResult);
            } catch (Exception e) {
                logger.error("put renderResult to barrierRenderResults error", e);
            }

            latch.countDown();

        }
    }

}
