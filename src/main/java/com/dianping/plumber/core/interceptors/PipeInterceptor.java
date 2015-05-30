package com.dianping.plumber.core.interceptors;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.dianping.plumber.core.*;
import com.dianping.plumber.core.concurrent.Executor;
import com.dianping.plumber.core.definitions.PlumberControllerDefinition;
import com.dianping.plumber.core.definitions.PlumberPipeDefinition;
import com.dianping.plumber.core.monitor.Monitor;
import com.dianping.plumber.core.monitor.MonitorEvent;
import com.dianping.plumber.core.workers.PlumberPipeWorker;
import com.dianping.plumber.exception.PlumberRuntimeException;
import com.dianping.plumber.utils.CollectionUtils;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-11
 * Time: PM10:03
 * To change this template use File | Settings | File Templates.
 */
public class PipeInterceptor implements Interceptor {

    private Logger logger = Logger.getLogger(PipeInterceptor.class);

    @Override
    public ResultType intercept(InvocationContext invocation) throws Exception {

        String controllerName = invocation.getControllerName();
        PlumberControllerDefinition controllerDefinition = PlumberWorkerDefinitionsRepo
            .getPlumberControllerDefinition(controllerName);
        List<PlumberPipeDefinition> pipeDefinitions = controllerDefinition.getPipeDefinitions();

        if (!CollectionUtils.isEmpty(pipeDefinitions)) {

            Map<String, Object> paramsFromRequest = invocation.getParamsFromRequest();
            Map<String, Object> paramsFromController = invocation.getParamsForPagelets();
            LinkedBlockingQueue<String> pipeRenderResultQueue = invocation
                .getPipeRenderResultQueue();
            Date startTime = invocation.getStartTime();
            ResultReturnedFlag resultReturnedFlag = invocation.getResultReturnedFlag();
            boolean hasPriority = controllerDefinition.isHasPriority();
            MonitorEvent monitorEvent = null;
            if (hasPriority) {
                monitorEvent = new MonitorEvent(pipeDefinitions, pipeRenderResultQueue, startTime,
                    resultReturnedFlag);
                Monitor.listen(monitorEvent);
            }

            for (PlumberPipeDefinition definition : pipeDefinitions) {
                String name = definition.getName();
                PlumberPagelet pipe = (PlumberPagelet) invocation.getApplicationContext().getBean(
                    name);
                injectAnnotationFields(pipe, definition, paramsFromRequest, paramsFromController);
                PlumberPipeWorker pipeWorker;
                if (!hasPriority) {
                    pipeWorker = new PlumberPipeWorker(definition, paramsFromRequest,
                        paramsFromController, pipe, pipeRenderResultQueue, startTime,
                        resultReturnedFlag);
                } else {
                    pipeWorker = new PlumberPipeWorker(definition, paramsFromRequest,
                        paramsFromController, pipe, monitorEvent, resultReturnedFlag);
                }
                Executor.getInstance().submit(pipeWorker);
            }

        }
        return ResultType.SUCCESS;
    }

    private void injectAnnotationFields(PlumberPagelet pipe, PlumberPipeDefinition pipeDefinition,
                                        Map<String, Object> paramsFromRequest,
                                        Map<String, Object> paramsFromController) {

        String pipeName = pipeDefinition.getName();
        List<Field> paramFromRequestFields = pipeDefinition.getParamFromRequestFields();
        List<Field> paramFromControllerFields = pipeDefinition.getParamFromControllerFields();

        injectAnnotationFields(pipeName, pipe, paramFromRequestFields, paramsFromRequest);
        injectAnnotationFields(pipeName, pipe, paramFromControllerFields, paramsFromController);

    }

    private void injectAnnotationFields(String pipeName, PlumberPagelet pipe, List<Field> fields,
                                        Map<String, Object> params) {

        if (!CollectionUtils.isEmpty(fields) && params != null) {
            for (Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = params.get(fieldName);
                if (fieldValue != null) {
                    try {
                        field.set(pipe, fieldValue);
                    } catch (Exception e) {
                        String msg = "inject annotation field of " + fieldName + " for pipe "
                                     + pipeName + " failure";
                        logger.error(msg, new PlumberRuntimeException(msg, e));
                    }
                }
            }
        }

    }

}
