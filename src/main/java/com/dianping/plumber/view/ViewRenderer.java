package com.dianping.plumber.view;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-29
 * Time: PM1:50
 * To change this template use File | Settings | File Templates.
 */
public interface ViewRenderer {

    public String render(String viewName, String viewSource, Map<String, Object> modelForView);

}
