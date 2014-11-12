package com.dianping.plumber.core;

import com.dianping.plumber.exception.PlumberInitializeFailureException;
import com.dianping.plumber.exception.PlumberRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
                    if ( beanDefinition.isSingleton() ) {
                        try {
                            Class c = Class.forName(beanClassName);
                            if ( PlumberController.class.isAssignableFrom(c) ) {
                                beanDefinition.setScope("prototype"); // reset PlumberController scope
                                PlumberWorkerDefinitionsRepo.controllerRegister(beanDefinitionName);
                            } else if ( PlumberPipe.class.isAssignableFrom(c) ) {
                                beanDefinition.setScope("prototype"); // reset PlumberPipe scope
                                PlumberWorkerDefinitionsRepo.pipeRegister(beanDefinitionName);
                            } else if ( PlumberBarrier.class.isAssignableFrom(c) ) {
                                beanDefinition.setScope("prototype"); // reset PlumberBarrier scope
                                PlumberWorkerDefinitionsRepo.barrierRegister(beanDefinitionName);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new PlumberInitializeFailureException(e);
                        }
                    } else if ( "com.dianping.plumber.core.Plumber".equals(beanClassName) ) {
                        beanDefinition.setScope("singleton"); // reset Plumber scope
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




}
