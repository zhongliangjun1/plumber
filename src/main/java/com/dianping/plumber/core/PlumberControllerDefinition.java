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

    private String name;
    private String viewName;
    private String viewSource;

    private List<String> barrierNames;
    private List<PlumberBarrierDefinition> barrierDefinitions;

    private List<String> pipeNames;
    private List<PlumberPipeDefinition> pipeDefinitions;




    public List<String> getBarrierNames() {
        return barrierNames;
    }

    public void setBarrierNames(List<String> barrierNames) {
        this.barrierNames = barrierNames;
    }

    public List<PlumberBarrierDefinition> getBarrierDefinitions() {
        return barrierDefinitions;
    }

    public void setBarrierDefinitions(List<PlumberBarrierDefinition> barrierDefinitions) {
        this.barrierDefinitions = barrierDefinitions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
