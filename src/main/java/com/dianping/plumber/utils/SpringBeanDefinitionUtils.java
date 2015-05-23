/**
 * liangjun.zhong
 * Copyright (c) 2014-2015 All Rights Reserved.
 */
package com.dianping.plumber.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;

import com.dianping.plumber.core.Plumber;
import com.dianping.plumber.core.PlumberController;
import com.dianping.plumber.core.PlumberPagelet;
import com.dianping.plumber.core.PlumberWorkerDefinitionsRepo;
import com.dianping.plumber.exception.PlumberInitializeFailureException;

/**
 * @author zhongliangjun1@gmail.com
 * @version $Id: SpringBeanDefinitionUtils.java, v 0.1 5/23/15 3:33 PM liangjun.zhong Exp $$
 */
public abstract class SpringBeanDefinitionUtils {

    public static void resetPlumberWorkerScopeAndRegister(ConfigurableListableBeanFactory beanFactory) {

        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        if (ArrayUtils.isEmpty(beanDefinitionNames))
            return;

        for (String beanDefinitionName : beanDefinitionNames) {

            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();

            if (StringUtils.isEmpty(beanClassName))
                continue;

            try {
                Class clazz = Class.forName(beanClassName);
                if (PlumberController.class.isAssignableFrom(clazz)) {

                    // reset PlumberController scope
                    beanDefinition.setScope("prototype");
                    String controllerViewPath = getViewPath(beanDefinitionName, beanDefinition);
                    List<String> barrierNamesInSpringBeanConfig = getBarrierNames(beanDefinition);
                    List<String> pipeNamesInSpringBeanConfig = getPipeNames(beanDefinition);
                    PlumberWorkerDefinitionsRepo.controllerRegister(beanDefinitionName,
                        controllerViewPath, clazz, barrierNamesInSpringBeanConfig,
                        pipeNamesInSpringBeanConfig);

                } else if (PlumberPagelet.class.isAssignableFrom(clazz)) {

                    // reset PlumberPagelet scope
                    beanDefinition.setScope("prototype");
                    String pageletViewPath = getViewPath(beanDefinitionName, beanDefinition);
                    PlumberWorkerDefinitionsRepo.pageletRegister(beanDefinitionName,
                        pageletViewPath, clazz);

                } else if (Plumber.class.isAssignableFrom(clazz)) {

                    // reset Plumber scope
                    beanDefinition.setScope("singleton");

                }
            } catch (ClassNotFoundException e) {
                throw new PlumberInitializeFailureException(e);
            }
        }

    }

    private static List<String> getBarrierNames(BeanDefinition beanDefinition) {
        return getPageletNames("barrierNames", beanDefinition);
    }

    private static List<String> getPipeNames(BeanDefinition beanDefinition) {
        return getPageletNames("pipeNames", beanDefinition);
    }

    private static List<String> getPageletNames(String propertyName, BeanDefinition beanDefinition) {
        List<String> names = new ArrayList<String>();

        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        PropertyValue propertyValue = propertyValues.getPropertyValue(propertyName);
        if (propertyValue != null) {
            ManagedList<TypedStringValue> managedList = (ManagedList<TypedStringValue>) propertyValue
                .getValue();
            if (!CollectionUtils.isEmpty(managedList)) {
                for (TypedStringValue typedStringValue : managedList) {
                    names.add(typedStringValue.getValue());
                }
            }
        }

        return names;
    }

    private static String getViewPath(String beanName, BeanDefinition beanDefinition) {

        String viewPath = null;
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        PropertyValue propertyValue = propertyValues.getPropertyValue("viewPath");
        if (propertyValue != null) {
            TypedStringValue typedStringValue = (TypedStringValue) propertyValue.getValue();
            String value = typedStringValue.getValue();
            if (StringUtils.isNotEmpty(value)) {
                viewPath = value;
            }
        }

        if (StringUtils.isEmpty(viewPath))
            throw new PlumberInitializeFailureException(beanName + " can not without viewPath");

        return viewPath;
    }

}
