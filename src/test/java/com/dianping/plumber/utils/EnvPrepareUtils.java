package com.dianping.plumber.utils;

import org.apache.log4j.xml.DOMConfigurator;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-10-31
 * Time: PM9:34
 * To change this template use File | Settings | File Templates.
 */
public class EnvPrepareUtils {

    /**
     * 手动初始化log4j
     */
    public static void startLog4j(){
        java.net.URL url = EnvPrepareUtils.class.getResource("/log/log4j.xml");
        DOMConfigurator.configure(url);
    }

}
