package com.dianping.plumber.config;

import ognl.Ognl;
import ognl.OgnlException;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author ltebean
 */
public class Yaml {

    private final org.yaml.snakeyaml.Yaml yaml = new  org.yaml.snakeyaml.Yaml();

    private Object config;

    private Yaml(){}

    public Yaml(InputStream inputStream){
        config = yaml.load(inputStream);
    }

    public Yaml(Reader reader){
        config = yaml.load(reader);
    }

    public Yaml(String string){
        config = yaml.load(string);
    }

    public <T> T get(String expression,Class<T> clazz){
        try {
            final Object ognlTree = Ognl.parseExpression(expression);
            return (T) Ognl.getValue(ognlTree, config, clazz);
        } catch (OgnlException e) {
            throw new RuntimeException("falied to get config with expression: "+expression,e);
        }
    }

    public <T> T get(String expression, T defaultValue, Class<T> clazz){
        try {
            T value = get(expression,clazz);
            return value!=null?value:defaultValue;
        } catch(Exception e) {
            return defaultValue;
        }
    }

}
