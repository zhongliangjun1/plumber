package com.dianping.plumber.core;

import com.dianping.plumber.core.interceptors.BarrierInterceptor;
import com.dianping.plumber.core.interceptors.ControllerInterceptor;
import com.dianping.plumber.core.interceptors.Interceptor;
import com.dianping.plumber.core.interceptors.PipeInterceptor;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: PM5:35
 * To change this template use File | Settings | File Templates.
 */
public class InvocationContext {

    private final String controllerName;
    private final Map<String, Object> paramsForController;
    private final ConcurrentHashMap<String, Object> modelForControllerView = new ConcurrentHashMap<String, Object>();
    private final ConcurrentHashMap<String, Object> paramsForPagelets = new ConcurrentHashMap<String, Object>();
    private final Iterator<Interceptor> interceptors;
    private final ApplicationContext applicationContext;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final LinkedBlockingQueue<String> pipeRenderResultQueue = new LinkedBlockingQueue<String>();

    private ResultType resultType;


    public InvocationContext(String controllerName, ApplicationContext applicationContext, Map<String, Object> paramsForController,
                             HttpServletRequest request, HttpServletResponse response) {
        this.controllerName = controllerName;
        this.paramsForController = paramsForController;
        this.applicationContext = applicationContext;
        this.request = request;
        this.response = response;
        this.interceptors = getInterceptors();
    }

    public ResultType invoke() throws Exception {
        if( interceptors.hasNext() ){
            final Interceptor interceptor = interceptors.next();
            resultType = interceptor.intercept(this);
        }
        return resultType;
    }

    private Iterator<Interceptor> getInterceptors() {
        List<Interceptor> interceptorList = new ArrayList<Interceptor>();
        interceptorList.add(new ControllerInterceptor());
        interceptorList.add(new BarrierInterceptor());
        interceptorList.add(new PipeInterceptor());
        return interceptorList.iterator();
    }

    public LinkedBlockingQueue<String> getPipeRenderResultQueue() {
        return pipeRenderResultQueue;
    }

    public String getControllerName() {
        return controllerName;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Map<String, Object> getParamsForController() {
        return paramsForController;
    }

    public Map<String, Object> getParamsFromRequest() {
        return paramsForController;
    }

    public ConcurrentHashMap<String, Object> getModelForControllerView() {
        return modelForControllerView;
    }

    public ConcurrentHashMap<String, Object> getParamsForPagelets() {
        return paramsForPagelets;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

}
