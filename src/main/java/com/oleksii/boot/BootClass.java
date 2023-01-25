package com.oleksii.boot;

import com.oleksii.config.context.ApplicationContextImpl;
import com.oleksii.config.exceptions.NoSuchBeanException;
import com.oleksii.config.exceptions.NoUniqueBeanException;
import com.oleksii.entities.FirstTestClass;
import com.oleksii.entities.NoAnnotationClass;
import com.oleksii.entities.SecondTestClass;
import com.oleksii.services.MainService;

import java.lang.reflect.InvocationTargetException;

public class BootClass {
    public static void main(String[] args) throws NoSuchBeanException, NoUniqueBeanException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        //Initial DI Demo instead of real tests
        ApplicationContextImpl applicationContext = new ApplicationContextImpl("com.oleksii");

        var firstTestClass = applicationContext.getBean(FirstTestClass.class);
        System.out.println(firstTestClass);
        var beansByName = applicationContext.getAllBeans(FirstTestClass.class);
        beansByName.entrySet().forEach(System.out::println);

        var annotatedClass = applicationContext.getBean(SecondTestClass.class);
        System.out.println(annotatedClass);

        try {
            applicationContext.getBean(NoAnnotationClass.class);
        } catch (NoSuchBeanException e) {
            System.out.println("Expected NoSuchBeanException");
        }

        MainService mainService = applicationContext.getBean(MainService.class);
        mainService.invokeInnerService();

    }
}
