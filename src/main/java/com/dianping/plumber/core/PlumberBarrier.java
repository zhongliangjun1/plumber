package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-8
 * Time: PM4:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberBarrier implements PlumberPagelet {

    protected String viewName;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

}
