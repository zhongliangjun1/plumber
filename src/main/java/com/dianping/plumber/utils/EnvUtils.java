package com.dianping.plumber.utils;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.core.PlumberGlobals;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 15/3/18
 * Time: PM10:26
 * To change this template use File | Settings | File Templates.
 */
public class EnvUtils {

    public static boolean isDev() {
        return PlumberGlobals.DEV_ENV.equals(PlumberConfig.getEnv());
    }

}
