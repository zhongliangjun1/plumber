package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-28
 * Time: PM10:53
 * Pagelets to render parts of your response afterwards
 */
public abstract class PlumberPipe implements PlumberPagelet  {

    protected String viewPath;

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
}
