package com.dianping.plumber.core.interceptors;

import com.dianping.plumber.core.*;
import com.dianping.plumber.exception.PlumberControllerNotFoundException;
import com.dianping.plumber.utils.CollectionUtils;
import com.dianping.plumber.utils.ResponseUtils;
import com.dianping.plumber.view.ViewRenderer;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: PM10:01
 * To change this template use File | Settings | File Templates.
 */
public class ControllerInterceptor implements Interceptor {

    @Override
    public ResultType intercept(InvocationContext invocation) throws Exception {
        String controllerName = invocation.getControllerName();
        ApplicationContext applicationContext = invocation.getApplicationContext();
        PlumberController controller = (PlumberController) applicationContext.getBean(controllerName);
        if ( controller==null ) {
            throw new PlumberControllerNotFoundException("can not find your plumberController : "+controllerName+" in spring applicationContext");
        }
        Map<String, Object> paramsForController = invocation.getParamsForController();
        ConcurrentHashMap<String, Object> modelForControllerView = invocation.getModelForControllerView();
        ConcurrentHashMap<String, Object> paramsForPagelets = invocation.getParamsForPagelets();

        ResultType resultType = controller.execute(paramsForController, paramsForPagelets, modelForControllerView);
        if ( resultType!=ResultType.SUCCESS )
            return resultType;

        resultType = invocation.invoke();
        if ( resultType!=ResultType.SUCCESS )
            return resultType;

        PlumberControllerDefinition controllerDefinition = PlumberWorkerDefinitionsRepo.getPlumberControllerDefinition(controllerName);
        String viewSource = controllerDefinition.getViewSource();
        ViewRenderer viewRenderer = PlumberWorkerDefinitionsRepo.getViewRenderer();
        String renderResult = viewRenderer.render(controllerName, viewSource, modelForControllerView);

        HttpServletResponse response = invocation.getResponse();
        ResponseUtils.flushBuffer(response, renderResult);

        List<PlumberPipeDefinition> pipeDefinitions = controllerDefinition.getPipeDefinitions();
        if ( !CollectionUtils.isEmpty(pipeDefinitions) ) {
            int pipeNum = pipeDefinitions.size();
            LinkedBlockingQueue<String> pipeRenderResultQueue = invocation.getPipeRenderResultQueue();
            while ( pipeNum>0 ) {
                String pipeRenderResult = pipeRenderResultQueue.take();
                if ( !PlumberGlobals.EMPTY_RENDER_RESULT.equals(pipeRenderResult) ) {
                    ResponseUtils.flushBuffer(response, pipeRenderResult);
                }
                pipeNum = pipeNum-1;
            }
        }

        ResponseUtils.flushBuffer(response, PlumberGlobals.CHUNKED_END);
        return ResultType.SUCCESS;
    }

}
