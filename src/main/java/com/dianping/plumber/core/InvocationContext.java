package com.dianping.plumber.core;

import com.dianping.plumber.core.interceptors.Interceptor;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: PM5:35
 * To change this template use File | Settings | File Templates.
 */
public class InvocationContext {

    private final PlumberController controller;
    private final Map<String, Object> paramsForController;
    private final ConcurrentHashMap<String, Object> modelForControllerView;
    private final ConcurrentHashMap<String, Object> paramsForPagelets;

    private final Iterator<Interceptor> interceptors;
    private ResultType resultType;
    private String controllerRenderResult;

    public InvocationContext(PlumberController controller, Map<String, Object> paramsForController,
                             ConcurrentHashMap<String, Object> modelForControllerView, ConcurrentHashMap<String, Object> paramsForPagelets) {
        this.controller = controller;
        this.paramsForController = paramsForController;
        this.modelForControllerView = modelForControllerView;
        this.paramsForPagelets = paramsForPagelets;
        this.interceptors = null;
    }

    public ResultType invoke() throws Exception {
        if( interceptors.hasNext() ){
            final Interceptor interceptor = interceptors.next();
            resultType = interceptor.intercept(this);
        }
        return resultType;
    }

    public PlumberController getController() {
        return controller;
    }

    public Map<String, Object> getParamsForController() {
        return paramsForController;
    }

    public ConcurrentHashMap<String, Object> getModelForControllerView() {
        return modelForControllerView;
    }

    public ConcurrentHashMap<String, Object> getParamsForPagelets() {
        return paramsForPagelets;
    }

    public String getControllerRenderResult() {
        return controllerRenderResult;
    }

    public void setControllerRenderResult(String controllerRenderResult) {
        this.controllerRenderResult = controllerRenderResult;
    }
}
