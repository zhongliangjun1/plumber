package com.dianping.plumber.core.interceptors;

import com.dianping.plumber.core.*;
import com.dianping.plumber.core.concurrent.Executor;
import com.dianping.plumber.exception.PlumberRuntimeException;
import com.dianping.plumber.utils.CollectionUtils;

import java.lang.reflect.Field;
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
public class BarrierInterceptor implements Interceptor {


    @Override
    public ResultType intercept(InvocationContext invocation) throws Exception {

        String controllerName = invocation.getControllerName();
        PlumberControllerDefinition controllerDefinition = PlumberWorkerDefinitionsRepo.getPlumberControllerDefinition(controllerName);

        if ( !CollectionUtils.isEmpty(controllerDefinition.getBarrierNames()) ) {

            List<PlumberBarrierDefinition> barrierDefinitions = controllerDefinition.getBarrierDefinitions();
            CountDownLatch barrierLatch = new CountDownLatch(barrierDefinitions.size());
            Map<String, Object> paramsFromRequest = invocation.getParamsFromRequest();
            Map<String, Object> paramsFromController = invocation.getParamsForPagelets();
            ConcurrentHashMap<String,String> barrierRenderResults = new ConcurrentHashMap<String, String>();

            for ( PlumberBarrierDefinition barrierDefinition : barrierDefinitions ) {
                String barrierName = barrierDefinition.getName();
                PlumberBarrier barrier = (PlumberBarrier) invocation.getApplicationContext().getBean(barrierName);
                injectAnnotationFields(barrier, barrierDefinition, paramsFromRequest, paramsFromController);
                PlumberBarrierWorker barrierWorker = new PlumberBarrierWorker(barrierDefinition, paramsFromController,
                        barrierLatch, barrier, barrierRenderResults);
                Executor.getInstance().submit(barrierWorker);
            }

            invocation.invoke();

            barrierLatch.await();
            ConcurrentHashMap<String, Object> modelForControllerView = invocation.getModelForControllerView();
            modelForControllerView.putAll(barrierRenderResults);
        }
        return ResultType.SUCCESS;
    }

    private void injectAnnotationFields(PlumberBarrier barrier,
                                        PlumberBarrierDefinition barrierDefinition,
                                        Map<String, Object> paramsFromRequest,
                                        Map<String, Object> paramsFromController) {

        String barrierName = barrierDefinition.getName();
        List<Field> paramFromRequestFields = barrierDefinition.getParamFromRequestFields();
        List<Field> paramFromControllerFields = barrierDefinition.getParamFromControllerFields();

        injectAnnotationFields(barrierName, barrier, paramFromRequestFields, paramsFromRequest);
        injectAnnotationFields(barrierName, barrier, paramFromControllerFields, paramsFromController);

    }

    private void injectAnnotationFields(String barrierName,
                                        PlumberBarrier barrier,
                                        List<Field> fields,
                                        Map<String, Object> params) {

        if ( !CollectionUtils.isEmpty(fields) && params!=null ) {
            for ( Field field : fields ) {
                String fieldName = field.getName();
                Object fieldValue = params.get(fieldName);
                if( fieldValue!=null ){
                    try {
                        field.set(barrier, fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new PlumberRuntimeException("inject annotation field of " + fieldName + " for barrier "+ barrierName + "failure", e);
                    }
                }
            }
        }

    }


}
