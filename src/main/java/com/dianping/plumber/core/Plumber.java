package com.dianping.plumber.core;

import com.dianping.plumber.exception.PlumberControllerNotFoundException;
import com.dianping.plumber.exception.PlumberInitializeFailureException;
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

    public void execute(String plumberControllerName, Map<String, Object> paramsForController,
                        HttpServletRequest request,  HttpServletResponse response) {



    }






    private PlumberController getPlumberController(String controllerName) {
        PlumberController controller = (PlumberController) applicationContext.getBean(controllerName);
        if ( controller==null ) {
            throw new PlumberControllerNotFoundException("can not find your plumberController : "+controllerName+" in spring applicationContext");
        }
        return controller;
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        prepareWorkerDefinitions(applicationContext);
    }

    private static volatile boolean hasPrepared = false;
    /**
     * prepare definitions of PlumberController and PlumberPipe
     * @param applicationContext
     */
    private static void prepareWorkerDefinitions(ApplicationContext applicationContext) {
        if ( !hasPrepared ) {
            PlumberWorkerDefinitionsRepo.prepareWorkerDefinitions(applicationContext);
        }
    }



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        resetPlumberWorkerScopeAndRegister(beanFactory);
    }

    private static volatile boolean hasReset = false;
    /**
     * reset PlumberController and PlumberPipe scope to be prototype
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
                                PlumberWorkerDefinitionsRepo.controllerRegister(beanDefinitionName); // register
                            } else if ( PlumberPipe.class.isAssignableFrom(c) ) {
                                beanDefinition.setScope("prototype"); // reset PlumberPipe scope
                                PlumberWorkerDefinitionsRepo.pipeRegister(beanDefinitionName); // register
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




}
