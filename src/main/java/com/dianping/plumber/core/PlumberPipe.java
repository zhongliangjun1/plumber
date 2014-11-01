package com.dianping.plumber.core;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-28
 * Time: PM10:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberPipe  {

    /**
     * is required to be responded at first
     */
    private boolean required;


    public abstract ResultType execute(Map<String, Object> params, Map<String, Object> modelForView);


    /**
     * is required to be responded at first of this pipe
     */
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
