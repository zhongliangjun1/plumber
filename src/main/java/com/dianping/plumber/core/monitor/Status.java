/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.core.monitor;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: Status.java, v 0.1 5/24/15 11:04 AM liangjun.zhong Exp $$
 */
public enum Status {

    INPROCESS(0, "in process"), SUBMIT(1, "submit"), FINISH(-1, "finish");

    public final int    code;
    public final String description;

    private Status(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
