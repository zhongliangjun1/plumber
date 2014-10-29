package com.dianping.plumber;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-28
 * Time: PM10:53
 * To change this template use File | Settings | File Templates.
 */
public interface PlumberBusiness {

    public ResultType execute(Map<String, Object> modelForView);

}
