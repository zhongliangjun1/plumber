package com.dianping.plumber.exception;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-12
 * Time: PM4:15
 * To change this template use File | Settings | File Templates.
 */
public class PlumberRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 4664098404877751078L;

    public PlumberRuntimeException() {
    }

    public PlumberRuntimeException(String s) {
        super(s);
    }

    public PlumberRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PlumberRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
