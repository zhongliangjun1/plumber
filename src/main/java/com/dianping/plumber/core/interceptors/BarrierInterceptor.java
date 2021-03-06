package com.dianping.plumber.core.interceptors;

import com.dianping.plumber.core.*;
import com.dianping.plumber.core.concurrent.Executor;
import com.dianping.plumber.core.definitions.PlumberBarrierDefinition;
import com.dianping.plumber.core.definitions.PlumberControllerDefinition;
import com.dianping.plumber.core.workers.PlumberBarrierWorker;
import com.dianping.plumber.exception.PlumberRuntimeException;
import com.dianping.plumber.utils.CollectionUtils;
import org.apache.log4j.Logger;

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

    private Logger logger = Logger.getLogger(BarrierInterceptor.class);


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
                PlumberPagelet barrier = (PlumberPagelet) invocation.getApplicationContext().getBean(barrierName);
                injectAnnotationFields(barrier, barrierDefinition, paramsFromRequest, paramsFromController);
                PlumberBarrierWorker barrierWorker = new PlumberBarrierWorker(barrierDefinition, paramsFromRequest, paramsFromController,
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

    private void injectAnnotationFields(PlumberPagelet barrier,
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
                                        PlumberPagelet barrier,
                                        List<Field> fields,
                                        Map<String, Object> params) {

        if ( !CollectionUtils.isEmpty(fields) && params!=null ) {
            for ( Field field : fields ) {
                String fieldName = field.getName();
                Object fieldValue = params.get(fieldName);
                if( fieldValue!=null ){
                    try {
                        field.set(barrier, fieldValue);
                    } catch (Exception e) {
                        String msg = "inject annotation field of " + fieldName + " for barrier "+ barrierName + " failure";
                        logger.error(msg, new PlumberRuntimeException(msg, e));
                    }
                }
            }
        }

    }


}
