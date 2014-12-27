package com.dianping.plumber.core;

import com.dianping.plumber.exception.PlumberInitializeFailureException;
import com.dianping.plumber.exception.PlumberRuntimeException;
import com.dianping.plumber.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
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
public class Plumber implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;


    public ResultType execute(String plumberControllerName, Map<String, Object> paramsForController,
                        HttpServletRequest request,  HttpServletResponse response) {
        InvocationContext invocationContext = new InvocationContext(plumberControllerName, applicationContext,
                paramsForController, request, response);
        try {
             return invocationContext.invoke();
        } catch (Exception e) {
            throw new PlumberRuntimeException(e);
        }
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        resetPlumberWorkerScopeAndRegister(beanFactory);
        prepareWorkerDefinitions();
    }

    private static volatile boolean hasReset = false;
    /**
     * reset PlumberPipe PlumberBarrier and PlumberController scope to be prototype
     * make sure Plumber to be singleton
     * register them to plumberWorkerDefinitionsRepo
     * @param beanFactory
     */
    private static void resetPlumberWorkerScopeAndRegister(ConfigurableListableBeanFactory beanFactory) {
        if ( !hasReset ) {
            hasReset = true;
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            if (beanDefinitionNames!=null && beanDefinitionNames.length>0) {
                for (String beanDefinitionName : beanDefinitionNames) {

                    BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                    String beanClassName = beanDefinition.getBeanClassName();
                    if ( StringUtils.isEmpty(beanClassName) )
                        continue;

                    try {
                        Class clazz = Class.forName(beanClassName);
                        if ( PlumberController.class.isAssignableFrom(clazz) ) {

                            beanDefinition.setScope("prototype"); // reset PlumberController scope
                            String controllerViewPath = getViewPath(beanDefinitionName, beanDefinition);
                            PlumberWorkerDefinitionsRepo.controllerRegister(beanDefinitionName, controllerViewPath, clazz);

                        } else if ( PlumberPipe.class.isAssignableFrom(clazz) ) {

                            beanDefinition.setScope("prototype"); // reset PlumberPipe scope
                            String pipeViewPath = getViewPath(beanDefinitionName, beanDefinition);
                            PlumberWorkerDefinitionsRepo.pipeRegister(beanDefinitionName, pipeViewPath, clazz);

                        } else if ( PlumberBarrier.class.isAssignableFrom(clazz) ) {

                            beanDefinition.setScope("prototype"); // reset PlumberBarrier scope
                            String barrierViewPath = getViewPath(beanDefinitionName, beanDefinition);
                            PlumberWorkerDefinitionsRepo.barrierRegister(beanDefinitionName, barrierViewPath, clazz);

                        } else if ( Plumber.class.isAssignableFrom(clazz) ) {

                            beanDefinition.setScope("singleton"); // reset Plumber scope

                        }
                    } catch (ClassNotFoundException e) {
                        throw new PlumberInitializeFailureException(e);
                    }

                }
            }
        }
    }

    private static volatile boolean hasPrepared = false;
    /**
     * prepare definitions of PlumberPipe PlumberBarrier and PlumberController
     */
    private static void prepareWorkerDefinitions() {
        if ( !hasPrepared ) {
            PlumberWorkerDefinitionsRepo.prepareWorkerDefinitions();
        }
    }

    private static String getViewPath(String beanName, BeanDefinition beanDefinition) {

        String viewPath = null;
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        PropertyValue propertyValue = propertyValues.getPropertyValue("viewPath");
        if ( propertyValue!=null ) {
            TypedStringValue typedStringValue = (TypedStringValue) propertyValue.getValue();
            String value = typedStringValue.getValue();
            if ( StringUtils.isNotEmpty(value) ) {
                viewPath = value;
            }
        }

        if ( StringUtils.isEmpty(viewPath) )
            throw new PlumberInitializeFailureException(beanName + " can not without viewPath");

        return viewPath;
    }


}
