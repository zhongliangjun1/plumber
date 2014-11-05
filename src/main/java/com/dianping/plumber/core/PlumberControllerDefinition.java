package com.dianping.plumber.core;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-5
 * Time: AM11:27
 * To change this template use File | Settings | File Templates.
 */
public class PlumberControllerDefinition {

    private String controllerName;
    private String viewName;
    private String viewSource;

    private List<String> pipeNames;
    private List<PlumberPipeDefinition> pipeDefinitions;

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewSource() {
        return viewSource;
    }

    public void setViewSource(String viewSource) {
        this.viewSource = viewSource;
    }

    public List<String> getPipeNames() {
        return pipeNames;
    }

    public void setPipeNames(List<String> pipeNames) {
        this.pipeNames = pipeNames;
    }

    public List<PlumberPipeDefinition> getPipeDefinitions() {
        return pipeDefinitions;
    }

    public void setPipeDefinitions(List<PlumberPipeDefinition> pipeDefinitions) {
        this.pipeDefinitions = pipeDefinitions;
    }
}
