package com.dianping.plumber.core;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-9
 * Time: AM12:11
 * To change this template use File | Settings | File Templates.
 */
public class PlumberBarrierDefinition extends PlumberPageletDefinition {

    private Class<PlumberBarrier> barrierClass;
    private List<Field> paramFromRequestFields;
    private List<Field> paramFromControllerFields;



    public Class<PlumberBarrier> getBarrierClass() {
        return barrierClass;
    }

    public void setBarrierClass(Class<PlumberBarrier> barrierClass) {
        this.barrierClass = barrierClass;
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
