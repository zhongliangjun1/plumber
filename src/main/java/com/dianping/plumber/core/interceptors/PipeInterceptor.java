package com.dianping.plumber.core.interceptors;

import com.dianping.plumber.core.*;
import com.dianping.plumber.core.concurrent.Executor;
import com.dianping.plumber.utils.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-11
 * Time: PM10:03
 * To change this template use File | Settings | File Templates.
 */
public class PipeInterceptor implements Interceptor {

    @Override
    public ResultType intercept(InvocationContext invocation) throws Exception {
        String controllerName = invocation.getControllerName();
        PlumberControllerDefinition controllerDefinition = PlumberWorkerDefinitionsRepo.getPlumberControllerDefinition(controllerName);
        List<PlumberPipeDefinition> pipeDefinitions = controllerDefinition.getPipeDefinitions();
        if ( !CollectionUtils.isEmpty(pipeDefinitions) ) {
            Map<String, Object> paramsFromController = invocation.getParamsForPagelets();
            HttpServletResponse response = invocation.getResponse();
            for (PlumberPipeDefinition definition : pipeDefinitions) {
                String name = definition.getName();
                PlumberPipe pipe = (PlumberPipe) invocation.getApplicationContext().getBean(name);
                PlumberPipeWorker pipeWorker = new PlumberPipeWorker(definition, paramsFromController, pipe, response);
                Executor.getInstance().submit(pipeWorker);
            }
        }
        return ResultType.SUCCESS;
    }


}
