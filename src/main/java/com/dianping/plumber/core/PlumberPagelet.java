package com.dianping.plumber.core;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-8
 * Time: PM4:45
 * To change this template use File | Settings | File Templates.
 */
public interface PlumberPagelet {

    public ResultType execute(Map<String, Object> paramsFromController, Map<String, Object> modelForView);

}
