package com.onlinebetting.web.bean;

import com.onlinebetting.web.annotation.Autowired;
import com.onlinebetting.web.annotation.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanLoader {

    public List<Object> loadServiceBean(String packageName) throws IllegalAccessException {
        return initializeServiceBean(packageName);
    }

    private List<Object> initializeServiceBean(String packageName) throws IllegalAccessException {

        Set<String> servicesClassNames = BeanScanner.scanServiceBeanClassNames(packageName);

        List<Object> serviceBeans = servicesClassNames.stream().map(SingletonBeanFactory::getBean).collect(Collectors.toList());

        for (Object bean : serviceBeans) {
            initializeBeanFields(bean.getClass(), bean, serviceBeans);
        }
        return serviceBeans;
    }

    public List<Object> loadControllerBean(String packageName, List<Object> serviceBeans) throws IllegalAccessException {
        return initializeControllerBean(packageName, serviceBeans);
    }

    private List<Object> initializeControllerBean(String packageName, List<Object> serviceBeans) throws IllegalAccessException {

        Set<String> controllerBeanNames = BeanScanner.scanControllerBeanClassNames(packageName);

        List<Object> controllerBeans = new ArrayList<>();
        for (String controllerBeanName : controllerBeanNames) {

            Object controllerBean = SingletonBeanFactory.getBean(controllerBeanName);
            Class<?> controllerClazz = controllerBean.getClass();

            initializeBeanFields(controllerClazz, controllerBean, serviceBeans);
            controllerBeans.add(controllerBean);
        }
        return controllerBeans;
    }

    private void initializeBeanFields(Class<?> clazz, Object targetBean, List<Object> serviceBeans) throws IllegalAccessException {

        Set<Field> fields = BeanScanner.getAnnotatedFields(clazz, Autowired.class);

        for (Field field : fields) {
            Object fieldBean = getFieldBean(serviceBeans, field);
            field.setAccessible(true);
            field.set(targetBean, fieldBean);
        }
    }

    private Object getFieldBean(List<Object> serviceBeans, Field field) {
        if (field.getType().isInterface()) {
            return serviceBeans.stream().filter(serviceObj -> this.isImplementationClass(field.getType(), serviceObj.getClass())).findFirst().orElse(null);
        } else {
            Object fieldBean = SingletonBeanFactory.getBean(field.getType().getName());
            Class<?> fieldClass = fieldBean.getClass();
            if (!fieldClass.isAnnotationPresent(Service.class)) {
                throw new IllegalArgumentException("Please declare the class annotation...");
            }
            return fieldBean;
        }
    }

    private boolean isImplementationClass(Class<?> serviceInterface, Class<?> serviceImpl) {
        return !Modifier.isAbstract(serviceImpl.getModifiers()) && serviceInterface.isAssignableFrom(serviceImpl);
    }

}
