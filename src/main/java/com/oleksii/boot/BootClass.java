package com.oleksii.boot;

import com.oleksii.config.context.ApplicationContextImpl;
import com.oleksii.config.exceptions.NoSuchBeanException;
import com.oleksii.config.exceptions.NoUniqueBeanException;
import com.oleksii.entities.FirstTestClass;
import com.oleksii.entities.NoAnnotationClass;
import com.oleksii.entities.SecondTestClass;

public class BootClass {
    public static void main(String[] args) throws NoSuchBeanException, NoUniqueBeanException {
        ApplicationContextImpl applicationContext = new ApplicationContextImpl("com.oleksii");

        var beansByName = applicationContext.getAllBeans(FirstTestClass.class);
        beansByName.entrySet().stream().forEach(System.out::println);

        var annotatedClass = applicationContext.getBean(SecondTestClass.class);
        System.out.println(annotatedClass);

        try {
            applicationContext.getBean(NoAnnotationClass.class);
        } catch (NoSuchBeanException e) {
            System.out.println("Expected NoSuchBeanException");
        }

    }
}
