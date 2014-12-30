package com.dianping.plumber.config;

import com.dianping.plumber.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-12-29
 * Time: PM5:36
 * To change this template use File | Settings | File Templates.
 */
public abstract class PlumberConfigOverrider {

    private static Logger logger = Logger.getLogger(PlumberConfigOverrider.class);

    protected static final Map<String,Object> overrideConfigurations = new ConcurrentHashMap<String, Object>();


    /**
     * put your new configuration value into the overrideConfigurations with the configName provided in PlumberConfig
     */
    public abstract void override();



    public static <T> T getOverrideConfiguration(String configName) {

        T result = null;

        if ( StringUtils.isEmpty(configName) || overrideConfigurations.get(configName)==null )
            return result;

        try {
            result = (T) overrideConfigurations.get(configName);
        } catch (Exception e) {
            logger.error("something wrong with your override configuration :" + configName, e);
        }

        return result;
    }
}
