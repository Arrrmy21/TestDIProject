package com.oleksii.config.context;

import com.oleksii.config.annotations.Autowired;
import com.oleksii.config.annotations.Bean;
import com.oleksii.config.exceptions.NoSuchBeanException;
import com.oleksii.config.exceptions.NoUniqueBeanException;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationContextImpl implements ApplicationContext {

    Map<String, Object> beans = new HashMap<>();

    public ApplicationContextImpl(String packageName) throws NoUniqueBeanException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchBeanException {

        Reflections reflections = new Reflections(packageName);

        var beanAnnotatedClasses = reflections.getTypesAnnotatedWith(Bean.class);
        for (var bean : beanAnnotatedClasses) {
            processBean(bean);
        }
        processAutowiredBeans();
    }

    @Override
    public <T> T getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException {
        var filteredBeans = getAllBeans(beanType);
        if (filteredBeans.size() > 1) {
            throw new NoUniqueBeanException(beanType.getSimpleName());
        }
        return filteredBeans.values().stream()
                .findFirst()
                .map(beanType::cast)
                .orElseThrow(() -> new NoSuchBeanException("No beans registered by name: " + beanType));
    }

    @Override
    public <T> T getBean(String name, Class<T> beanType) throws NoSuchBeanException {
        var filteredBeans = beans.entrySet().stream()
                .filter(b -> b.getValue().equals(beanType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var bean = filteredBeans.get(name);
        if (filteredBeans.size() < 1 || bean == null) {
            throw new NoSuchBeanException("No beans registered by name: " + name);
        }
        return beanType.cast(bean);
    }

    @Override
    public <T> Map<String, Object> getAllBeans(Class<T> beanType) {
        return beans.entrySet().stream()
                .filter(b -> b.getValue().getClass().isAssignableFrom(beanType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void processBean(Class<?> potentialBean) throws NoUniqueBeanException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        var className = potentialBean.getSimpleName();
        var annotationBeanName = potentialBean.getAnnotation(Bean.class).value();
        String finalBeanName;
        if (annotationBeanName.isEmpty()) {
            finalBeanName = lowercaseFirstLetter(className);
        } else {
            finalBeanName = annotationBeanName;
        }
        if (beans.get(finalBeanName) != null) {
            throw new NoUniqueBeanException(finalBeanName);
        }
        var instance = potentialBean.getConstructor().newInstance();
        beans.put(finalBeanName, instance);
        System.out.println("Bean created!");
    }

    private String lowercaseFirstLetter(String value) {
        return String.valueOf(value.charAt(0)).toLowerCase().concat(value.substring(1));
    }

    private void processAutowiredBeans() throws NoUniqueBeanException, NoSuchBeanException, IllegalAccessException {
        for (Map.Entry entry : beans.entrySet()) {
            var bean = entry.getValue();
            for (var field : bean.getClass().getDeclaredFields()) {
                if (field.getAnnotation(Autowired.class) != null) {
                    var fieldName = field.getName();
                    var beanToBeInjected = getBeanToInject(fieldName);

                    field.setAccessible(true);
                    field.set(bean, beanToBeInjected);
                }
            }
        }
    }

    private Object getBeanToInject(String fieldName) throws NoUniqueBeanException, NoSuchBeanException {
        var requiredBeanName = beans.keySet().stream()
                .filter(f -> f.equals(fieldName)).toList();
        if (requiredBeanName.isEmpty()) {
            throw new NoSuchBeanException("No beans found for injection by name: " + fieldName);
        }
        if (requiredBeanName.size() > 1) {
            throw new NoUniqueBeanException("Several beans created with name: " + fieldName);
        }

        return beans.get(requiredBeanName.get(0));
    }

}
