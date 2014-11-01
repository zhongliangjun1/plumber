package com.dianping.plumber.core;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-1
 * Time: PM10:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberController {

    public abstract ResultType execute(Map<String, Object> modelForView);

}
