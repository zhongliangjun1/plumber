package com.dianping.plumber.utils;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.PlumberGlobals;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-2
 * Time: AM12:27
 * To change this template use File | Settings | File Templates.
 */
public class PlumberConfigUtils {

    public static String getViewEncoding() {
        return PlumberConfig.get("view.encoding", PlumberGlobals.DEFAULT_VIEW_ENCODING, String.class);
    }

    public static String getViewResourcesPath() {
        return PlumberConfig.get("view.resourcesPath", String.class);
    }

    public static String getViewSuffix() {
        return PlumberConfig.get("view.suffix", String.class);
    }



}
