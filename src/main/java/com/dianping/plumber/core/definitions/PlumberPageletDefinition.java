package com.dianping.plumber.core.definitions;

import java.lang.reflect.Field;
import java.util.List;

import com.dianping.plumber.core.PlumberPagelet;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-8
 * Time: PM4:56
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPageletDefinition {

    private String                name;

    private Class<PlumberPagelet> pageletClass;
    private List<Field>           paramFromRequestFields;
    private List<Field>           paramFromControllerFields;

    protected String              viewPath;
    protected String              viewSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<PlumberPagelet> getPageletClass() {
        return pageletClass;
    }

    public void setPageletClass(Class<PlumberPagelet> pageletClass) {
        this.pageletClass = pageletClass;
    }

    public List<Field> getParamFromRequestFields() {
        return paramFromRequestFields;
    }

    public void setParamFromRequestFields(List<Field> paramFromRequestFields) {
        this.paramFromRequestFields = paramFromRequestFields;
    }

    public List<Field> getParamFromControllerFields() {
        return paramFromControllerFields;
    }

    public void setParamFromControllerFields(List<Field> paramFromControllerFields) {
        this.paramFromControllerFields = paramFromControllerFields;
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
