package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-8
 * Time: PM4:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberBarrier implements PlumberPagelet {

    protected String viewPath;

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
}
