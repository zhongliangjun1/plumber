package com.dianping.plumber.core;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-5
 * Time: PM3:40
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPipeDefinition extends PlumberPageletDefinition {

    private Class<PlumberPipe> pipeClass;
    private List<Field> paramFromRequestFields;
    private List<Field> paramFromControllerFields;



    public Class<PlumberPipe> getPipeClass() {
        return pipeClass;
    }

    public void setPipeClass(Class<PlumberPipe> pipeClass) {
        this.pipeClass = pipeClass;
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
}
