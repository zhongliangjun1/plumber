package com.dianping.plumber.exception;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 15/1/19
 * Time: PM6:35
 * To change this template use File | Settings | File Templates.
 */
public class PlumberPipeTimeoutException extends PlumberRuntimeException {

    private static final long serialVersionUID = 2147641348805445352L;

    public PlumberPipeTimeoutException() {
    }

    public PlumberPipeTimeoutException(String message) {
        super(message);
    }

    public PlumberPipeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlumberPipeTimeoutException(Throwable cause) {
        super(cause);
    }
}
