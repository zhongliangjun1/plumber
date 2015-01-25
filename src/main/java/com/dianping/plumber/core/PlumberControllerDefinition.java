package com.dianping.plumber.core;

import java.lang.reflect.Field;
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
    private String viewPath;
    private String viewSource;

    private List<String> barrierNames;
    private List<PlumberBarrierDefinition> barrierDefinitions;

    private List<String> pipeNames;
    private List<PlumberPipeDefinition> pipeDefinitions;
    private Integer seqStartingLocation;

    private Class<PlumberController> controllerClass;
    private List<Field> paramFromRequestFields;



    public List<String> getBarrierNames() {
        return barrierNames;
    }

    public void setBarrierNames(List<String> barrierNames) {
        this.barrierNames = barrierNames;
    }

    public List<PlumberBarrierDefinition> getBarrierDefinitions() {
        return barrierDefinitions;
    }

    public Integer getSeqStartingLocation() {
        return seqStartingLocation;
    }

    public void setSeqStartingLocation(Integer seqStartingLocation) {
        this.seqStartingLocation = seqStartingLocation;
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

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
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

    public List<Field> getParamFromRequestFields() {
        return paramFromRequestFields;
    }

    public void setParamFromRequestFields(List<Field> paramFromRequestFields) {
        this.paramFromRequestFields = paramFromRequestFields;
    }

    public Class<PlumberController> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<PlumberController> controllerClass) {
        this.controllerClass = controllerClass;
    }
}
