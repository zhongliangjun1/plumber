package com.dianping.plumber.core;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-8
 * Time: PM4:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberPagelet {

    protected String viewPath;

    public abstract ResultType execute(Map<String, Object> paramsFromRequest, Map<String, Object> paramsFromController, Map<String, Object> modelForView);

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

}
