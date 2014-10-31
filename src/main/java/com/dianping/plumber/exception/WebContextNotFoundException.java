package com.dianping.plumber.exception;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-31
 * Time: PM9:56
 * To change this template use File | Settings | File Templates.
 */
public class WebContextNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -1295102063513232691L;

    public WebContextNotFoundException() {
    }

    public WebContextNotFoundException(String s) {
        super(s);
    }

    public WebContextNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public WebContextNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
