package com.oleksii.config.context;

import com.oleksii.config.annotations.Bean;
import com.oleksii.config.exceptions.NoSuchBeanException;
import com.oleksii.config.exceptions.NoUniqueBeanException;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationContextImpl implements ApplicationContext {

    Map<String, Class<?>> beans = new HashMap<>();

    public ApplicationContextImpl(String packageName) throws NoUniqueBeanException {

        Reflections reflections = new Reflections(packageName);

        var beanAnnotatedClasses = reflections.getTypesAnnotatedWith(Bean.class);
        for (var bean : beanAnnotatedClasses) {
            processBean(bean);
        }
    }

    @Override
    public <T> Class<?> getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException {
        var filteredBeans = beans.entrySet().stream()
                .filter(b -> b.getValue().equals(beanType)).toList();
        if (filteredBeans.size() > 1) {
            throw new NoUniqueBeanException(beanType.getSimpleName());
        } else if (filteredBeans.isEmpty()) {
            throw new NoSuchBeanException();
        }

        return filteredBeans.get(0).getValue();
    }

    @Override
    public <T> Class<?> getBean(String name, Class<T> beanType) throws NoSuchBeanException {

        var filteredBeans = beans.entrySet().stream()
                .filter(b -> b.getValue().equals(beanType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var bean = filteredBeans.get(name);
        if (filteredBeans.size() < 1 || bean == null) {
            throw new NoSuchBeanException();
        }
        return bean;
    }

    @Override
    public <T> Map<String, Class<?>> getAllBeans(Class<T> beanType) {
        return beans.entrySet().stream()
                .filter(b -> b.getValue().equals(beanType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void processBean(Class<?> potentialBean) throws NoUniqueBeanException {

        var className = potentialBean.getSimpleName();
        var annotationBeanName = potentialBean.getAnnotation(Bean.class).value();
        String finalBeanName = null;
        if (annotationBeanName.isEmpty()) {
            finalBeanName = lowercaseFirstLetter(className);
        } else {
            finalBeanName = annotationBeanName;

        }
        if (beans.get(finalBeanName) != null) {
            throw new NoUniqueBeanException(finalBeanName);
        }
        beans.put(finalBeanName, potentialBean);
        System.out.println("Bean created!");
    }

    private String lowercaseFirstLetter(String value) {
        return String.valueOf(value.charAt(0)).toLowerCase().concat(value.substring(1));
    }
}
