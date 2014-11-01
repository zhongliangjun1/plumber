package com.dianping.plumber.config;

import com.dianping.plumber.core.PlumberGlobals;

import java.io.InputStream;

/**
 * @author ltebean
 */
public class Configuration {

    private static final Yaml yaml;

    static{
        InputStream input = Configuration.class.getResourceAsStream(PlumberGlobals.CONFIG_PATH);
        yaml = new Yaml(input);
    }

    public static <T> T get(String expression,Class<T> clazz){
        return yaml.get(expression, clazz);
    }

    public static <T> T get(String expression, T defaultValue, Class<T> clazz){
        return yaml.get(expression, defaultValue, clazz);
    }

}
