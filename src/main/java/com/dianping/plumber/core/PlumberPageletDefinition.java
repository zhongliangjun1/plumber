package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-8
 * Time: PM4:56
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPageletDefinition {

    protected String name;
    protected String viewPath;
    protected String viewSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewSource() {
        return viewSource;
    }

    public void setViewSource(String viewSource) {
        this.viewSource = viewSource;
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
}
