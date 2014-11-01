package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-28
 * Time: PM10:56
 * To change this template use File | Settings | File Templates.
 */
public enum ResultType {

    SUCCESS("success"),
    NONE("none"),
    ERROR("error");

    public String value;

    private ResultType(String value) {
        this.value = value;
    }

}
