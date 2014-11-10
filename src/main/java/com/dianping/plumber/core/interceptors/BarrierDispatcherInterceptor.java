package com.dianping.plumber.core.interceptors;

import com.dianping.plumber.core.*;
import com.dianping.plumber.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: PM10:42
 * To change this template use File | Settings | File Templates.
 */
public class BarrierDispatcherInterceptor implements Interceptor {


    @Override
    public ResultType intercept(InvocationContext invocation) throws Exception {
        String controllerName = invocation.getControllerName();
        PlumberControllerDefinition controllerDefinition = PlumberWorkerDefinitionsRepo.getPlumberControllerDefinition(controllerName);
        if ( !CollectionUtils.isEmpty(controllerDefinition.getBarrierNames()) ) {
            List<PlumberBarrierDefinition> barrierDefinitions = controllerDefinition.getBarrierDefinitions();
            CountDownLatch barrierLatch = new CountDownLatch(barrierDefinitions.size());
            Map<String, Object> paramsFromController = invocation.getParamsForPagelets();
            ConcurrentHashMap<String,String> barrierRenderResults = new ConcurrentHashMap<String, String>();
            for ( PlumberBarrierDefinition barrierDefinition : barrierDefinitions ) {
                String barrierName = barrierDefinition.getName();
                PlumberBarrier barrier = (PlumberBarrier) invocation.getApplicationContext().getBean(barrierName);
                PlumberBarrierWorker barrierWorker = new PlumberBarrierWorker(barrierDefinition, paramsFromController,
                        barrierLatch, barrier, barrierRenderResults);
            }
            barrierLatch.await();
            ConcurrentHashMap<String, Object> modelForControllerView = invocation.getModelForControllerView();
            modelForControllerView.putAll(barrierRenderResults);
        }
        return invocation.invoke();
    }


}
