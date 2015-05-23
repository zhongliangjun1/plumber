package com.dianping.plumber.core;

import static com.dianping.plumber.utils.ResponseUtils.disableNginxProxyBuffering;
import static com.dianping.plumber.utils.ResponseUtils.setResponseContentType;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.dianping.plumber.exception.PlumberRuntimeException;
import com.dianping.plumber.utils.ConfigurationUtils;
import com.dianping.plumber.utils.SpringBeanDefinitionUtils;
import com.dianping.plumber.utils.StringUtils;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: Plumber.java, v 0.1 5/23/15 3:41 PM liangjun.zhong Exp $$
 */
public class Plumber implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static Logger      logger = Logger.getLogger(Plumber.class);

    private ApplicationContext applicationContext;

    public ResultType execute(String plumberControllerName,
                              Map<String, Object> paramsForController, HttpServletRequest request,
                              HttpServletResponse response) {

        validate(plumberControllerName, paramsForController, request, response);

        prepare(plumberControllerName, paramsForController, request, response);

        return invoke(plumberControllerName, paramsForController, request, response);
    }

    private void validate(String plumberControllerName, Map<String, Object> paramsForController,
                          HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isEmpty(plumberControllerName)
            || applicationContext.getBean(plumberControllerName) == null)
            throw new PlumberRuntimeException(new IllegalArgumentException(
                "invalid controllerName : " + plumberControllerName));

        if (response == null)
            throw new PlumberRuntimeException(new IllegalArgumentException(
                "response can not be null"));
    }

    private void prepare(String plumberControllerName, Map<String, Object> paramsForController,
                         HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(response);
        disableNginxProxyBuffering(plumberControllerName, response);
    }

    private ResultType invoke(String plumberControllerName,
                              Map<String, Object> paramsForController, HttpServletRequest request,
                              HttpServletResponse response) {
        try {
            InvocationContext invocationContext = new InvocationContext(plumberControllerName,
                applicationContext, paramsForController, request, response);
            return invocationContext.invoke();
        } catch (Exception e) {
            Exception runtimeException = new PlumberRuntimeException(e);
            logger.error("some exception occurred during the running time", runtimeException);
            return ResultType.ERROR;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        loadOverrideConfiguration();
        resetPlumberWorkerScopeAndRegister(beanFactory);
        prepareWorkerDefinitions();
    }

    private static volatile boolean hasOverride = false;

    private static void loadOverrideConfiguration() {
        if (!hasOverride) {
            ConfigurationUtils.loadOverrideConfiguration();
            hasOverride = true;
            logger.info("plumber : load override configurations success");
        }
    }

    private static volatile boolean hasReset = false;

    /**
     * 1. Reset PlumberPipe PlumberBarrier and PlumberController scope to be prototype
     * 2. Make sure Plumber scope to be singleton
     * 3. Register them to plumberWorkerDefinitionsRepo
     * @param beanFactory
     */
    private static void resetPlumberWorkerScopeAndRegister(ConfigurableListableBeanFactory beanFactory) {
        if (!hasReset) {
            SpringBeanDefinitionUtils.resetPlumberWorkerScopeAndRegister(beanFactory);
            hasReset = true;
            logger.info("plumber : reset worker's scope and register to definitions repo success");
        }
    }

    private static volatile boolean hasPrepared = false;

    /**
     * prepare definitions of PlumberPipe PlumberBarrier and PlumberController
     */
    private static void prepareWorkerDefinitions() {
        if (!hasPrepared) {
            PlumberWorkerDefinitionsRepo.prepareWorkerDefinitions();
            hasPrepared = true;
            logger.info("plumber : prepare worker's definitions success");
        }
    }

}
