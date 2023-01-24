package com.oleksii.config.context;

import com.oleksii.config.exceptions.NoSuchBeanException;
import com.oleksii.config.exceptions.NoUniqueBeanException;

import java.util.Map;

public interface ApplicationContext {

    <T> Class<?> getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException;

    <T> Class<?> getBean(String name, Class<T> beanType) throws NoSuchBeanException;

    <T> Map<String, Class<?>> getAllBeans(Class<T> beanType);
}
