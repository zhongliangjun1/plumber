package com.dianping.plumber.core;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 15/1/20
 * Time: AM12:12
 * To change this template use File | Settings | File Templates.
 */
public class ResultReturnedFlag {

    private volatile boolean isReturned = false;

    public void setReturned() {
        isReturned = true;
    }

    public boolean isReturned() {
        return isReturned;
    }

}
