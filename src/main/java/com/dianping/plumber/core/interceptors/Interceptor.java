package com.dianping.plumber.core.interceptors;

import com.dianping.plumber.core.InvocationContext;
import com.dianping.plumber.core.ResultType;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-10
 * Time: PM5:35
 * To change this template use File | Settings | File Templates.
 */
public interface Interceptor {

    public ResultType intercept(InvocationContext invocation) throws Exception;

}
