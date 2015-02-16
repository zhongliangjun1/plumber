package com.dianping.plumber.core;

import com.dianping.plumber.config.PlumberConfig;
import com.dianping.plumber.config.PlumberConfigOverrider;
import com.dianping.plumber.config.PlumberConfigOverriderFactory;
import com.dianping.plumber.exception.PlumberInitializeFailureException;
import com.dianping.plumber.exception.PlumberRuntimeException;
import com.dianping.plumber.utils.StringUtils;
import org.apache.log4j.Logger;
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

    private static Logger logger = Logger.getLogger(Plumber.class);

    private ApplicationContext applicationContext;


    public ResultType execute(String plumberControllerName, Map<String, Object> paramsForController,
                        HttpServletRequest request,  HttpServletResponse response) {

        validate(plumberControllerName, paramsForController, request, response);

        prepare(plumberControllerName, paramsForController, request, response);

        InvocationContext invocationContext = new InvocationContext(plumberControllerName, applicationContext,
                paramsForController, request, response);
        try {
             return invocationContext.invoke();
        } catch (Exception e) {
            Exception runtimeException = new PlumberRuntimeException(e);
            logger.error("some exception occured during the running time", runtimeException);
            return ResultType.ERROR;
        }
    }

    private void validate(String plumberControllerName, Map<String, Object> paramsForController,
                         HttpServletRequest request,  HttpServletResponse response) {

        if ( StringUtils.isEmpty(plumberControllerName) || applicationContext.getBean(plumberControllerName)==null )
            throw new PlumberRuntimeException(new IllegalArgumentException("invalid controllerName : " + plumberControllerName ));

        if ( response==null )
            throw new PlumberRuntimeException(new IllegalArgumentException("response can not be null"));
    }

    private void prepare(String plumberControllerName, Map<String, Object> paramsForController,
                         HttpServletRequest request,  HttpServletResponse response) {

        setResponseContentType(response);

        disableNginxProxyBuffering(response);

    }

    private void setResponseContentType(HttpServletResponse response) {

        if ( StringUtils.isNotEmpty(response.getContentType()) )
            return;

        String contentType = PlumberConfig.getResponseContentType();
        boolean noCharsetInContentType = true;
        if ( contentType.toLowerCase().indexOf("charset=")!=-1 )
            noCharsetInContentType = false;

        if ( noCharsetInContentType ) {
            response.setContentType(
                    contentType + "; charset=" + PlumberConfig.getViewEncoding());
        } else {
            response.setContentType(contentType);
        }
    }

    /**
     * When buffering is disabled, the response is passed to a client synchronously,
     * immediately as it is received. Nginx will not try to read the whole response from the proxied server.
     * To learn more : http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_buffering
     */
    private void disableNginxProxyBuffering(HttpServletResponse response) {
        response.setHeader("X-Accel-Buffering", "no");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        loadOverrideConfiguration();
        resetPlumberWorkerScopeAndRegister(beanFactory);
        prepareWorkerDefinitions();
    }

    private static void loadOverrideConfiguration() {
        String configOverriderFactory = PlumberConfig.getConfigOverriderFactory();
        if ( StringUtils.isNotEmpty(configOverriderFactory) ) {
            try {
                Class clazz = Class.forName(configOverriderFactory);
                PlumberConfigOverriderFactory factory = (PlumberConfigOverriderFactory) clazz.newInstance();
                PlumberConfigOverrider overrider = factory.getConfigOverrider();
                overrider.override();
            } catch (Exception e) {
                throw new PlumberInitializeFailureException("can not load your override configurations",e);
            }
            logger.info("plumber : load override configurations success");
        }
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

                        } else if ( PlumberPagelet.class.isAssignableFrom(clazz) ) {

                            beanDefinition.setScope("prototype"); // reset PlumberPagelet scope
                            String pageletViewPath = getViewPath(beanDefinitionName, beanDefinition);
                            PlumberWorkerDefinitionsRepo.pageletRegister(beanDefinitionName, pageletViewPath, clazz);

                        } else if ( Plumber.class.isAssignableFrom(clazz) ) {

                            beanDefinition.setScope("singleton"); // reset Plumber scope

                        }
                    } catch (ClassNotFoundException e) {
                        throw new PlumberInitializeFailureException(e);
                    }

                }
                logger.info("plumber : reset worker's scope and register to definitions repo success");
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
            logger.info("plumber : prepare worker's definitions success");
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
