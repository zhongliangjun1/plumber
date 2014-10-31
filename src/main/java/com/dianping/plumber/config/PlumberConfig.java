package com.dianping.plumber.config;

import com.dianping.plumber.PlumberConstants;
import java.io.InputStream;

/**
 * @author ltebean
 */
public class PlumberConfig {

    private static final Yaml yaml;

    static{
        InputStream input = PlumberConfig.class.getResourceAsStream(PlumberConstants.CONFIG_PATH);
        yaml = new Yaml(input);
    }


    public static <T> T get(String expression,Class<T> clazz){
        return yaml.get(expression,clazz);
    }

    public static <T> T get(String expression, T defaultValue, Class<T> clazz){
        return yaml.get(expression,defaultValue,clazz);
    }

}
