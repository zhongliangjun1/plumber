package com.dianping.plumber.config;

import com.dianping.plumber.core.PlumberGlobals;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-2
 * Time: AM12:27
 * To change this template use File | Settings | File Templates.
 */
public class PlumberConfig {

    public static String getViewEncoding() {
        return Configuration.get("view.encoding", PlumberGlobals.DEFAULT_VIEW_ENCODING, String.class);
    }

    public static String getViewResourcesPath() {
        return Configuration.get("view.resourcesPath", String.class);
    }

    public static String getViewSuffix() {
        return Configuration.get("view.suffix", String.class);
    }



}
