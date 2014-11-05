package com.dianping.plumber.exception;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-5
 * Time: PM2:18
 * To change this template use File | Settings | File Templates.
 */
public class PlumberInitializeFailureException extends RuntimeException {
    private static final long serialVersionUID = 7138653675628044423L;

    public PlumberInitializeFailureException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PlumberInitializeFailureException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PlumberInitializeFailureException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PlumberInitializeFailureException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
