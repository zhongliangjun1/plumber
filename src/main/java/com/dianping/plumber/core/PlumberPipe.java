package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-28
 * Time: PM10:53
 * Pagelets to render parts of your response afterwards
 */
public abstract class PlumberPipe implements PlumberPagelet  {

    protected String viewName;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
