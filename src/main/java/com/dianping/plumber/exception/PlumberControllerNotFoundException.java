package com.dianping.plumber.exception;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-2
 * Time: AM2:16
 * To change this template use File | Settings | File Templates.
 */
public class PlumberControllerNotFoundException extends PlumberInitializeFailureException {
    private static final long serialVersionUID = -4783103032962075203L;

    public PlumberControllerNotFoundException() {
    }

    public PlumberControllerNotFoundException(String s) {
        super(s);
    }

    public PlumberControllerNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PlumberControllerNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
