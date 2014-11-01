package com.dianping.plumber.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: liangjun.zhong
 * Date: 14-11-2
 * Time: AM12:59
 * To change this template use File | Settings | File Templates.
 */
public class Plumber implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void execute(String plumberControllerName, Map<String, Object> paramsForController,
                        HttpServletRequest request,  HttpServletResponse response) {

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
