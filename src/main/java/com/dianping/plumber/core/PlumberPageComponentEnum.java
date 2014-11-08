package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-9
 * Time: AM12:29
 * To change this template use File | Settings | File Templates.
 */
public enum PlumberPageComponentEnum {

    PlumberController("PlumberController"),
    PlumberBarrier("PlumberBarrier"),
    PlumberPipe("PlumberPipe");

    public String value;

    private PlumberPageComponentEnum(String value) {
        this.value = value;
    }


}
