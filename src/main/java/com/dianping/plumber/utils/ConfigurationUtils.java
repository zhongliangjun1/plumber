/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.utils;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.config.PlumberConfigOverrider;
import com.dianping.plumber.config.PlumberConfigOverriderFactory;
import com.dianping.plumber.exception.PlumberInitializeFailureException;
import org.apache.log4j.Logger;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: ConfigurationUtils.java, v 0.1 5/23/15 3:41 PM liangjun.zhong Exp $$
 */
public abstract class ConfigurationUtils {

    private static Logger logger = Logger.getLogger(ConfigurationUtils.class);

    public static void loadOverrideConfiguration() {
        String configOverriderFactory = PlumberConfig.getConfigOverriderFactory();
        if (StringUtils.isNotEmpty(configOverriderFactory)) {
            try {
                Class clazz = Class.forName(configOverriderFactory);
                PlumberConfigOverriderFactory factory = (PlumberConfigOverriderFactory) clazz
                    .newInstance();
                PlumberConfigOverrider overrider = factory.getConfigOverrider();
                overrider.override();
            } catch (Exception e) {
                throw new PlumberInitializeFailureException(
                    "can not load your override configurations", e);
            }
            logger.info("plumber : load override configurations success");
        }
    }

}
