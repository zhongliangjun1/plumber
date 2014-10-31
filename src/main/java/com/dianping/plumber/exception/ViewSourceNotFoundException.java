package com.dianping.plumber.exception;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-31
 * Time: PM10:57
 * To change this template use File | Settings | File Templates.
 */
public class ViewSourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 9054729895497097919L;

    public ViewSourceNotFoundException() {
    }

    public ViewSourceNotFoundException(String s) {
        super(s);
    }

    public ViewSourceNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ViewSourceNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
