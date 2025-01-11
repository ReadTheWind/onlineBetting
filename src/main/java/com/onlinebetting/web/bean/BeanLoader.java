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

        Set<String> services = BeanScanner.scanServiceBean(packageName);
        List<Object> serviceObjects = services.stream().map(SingletonBeanFactory::getBean).collect(Collectors.toList());

        for (Object serviceObj : serviceObjects) {
            setFields(serviceObj.getClass(), serviceObj, serviceObjects);
        }
        return serviceObjects;
    }

    public List<Object> loadControllerBean(String packageName, List<Object> serviceObjects) throws IllegalAccessException {
        return initializeControllerBean(packageName, serviceObjects);
    }

    private List<Object> initializeControllerBean(String packageName, List<Object> serviceObjects) throws IllegalAccessException {

        Set<String> controllerBeanNames = BeanScanner.scanControllerBean(packageName);

        List<Object> controllerBeans = new ArrayList<>();
        for (String controllerBeanName : controllerBeanNames) {

            Object controllerBean = SingletonBeanFactory.getBean(controllerBeanName);
            Class<?> controllerClazz = controllerBean.getClass();

            setFields(controllerClazz, controllerBean, serviceObjects);
            controllerBeans.add(controllerBean);
        }
        return controllerBeans;
    }

    private void setFields(Class<?> clazz, Object targetObj, List<Object> serviceObjs) throws IllegalAccessException {

        Set<Field> fields = BeanScanner.getAnnotatedFields(clazz, Autowired.class);

        for (Field field : fields) {
            if (!field.getType().isInterface()) {
                Object fieldObj = SingletonBeanFactory.getBean(field.getType().getName());
                Class<?> fieldClass = fieldObj.getClass();
                if (!fieldClass.isAnnotationPresent(Service.class)) {
                    throw new IllegalStateException("Please declare the class annotation...");
                }

                field.setAccessible(true);
                field.set(targetObj, fieldObj);
            } else {
                Object serviceObject = serviceObjs.stream().filter(serviceObj -> this.isImplementationClass(field.getType(), serviceObj.getClass())).findFirst().orElse(null);
                field.setAccessible(true);
                field.set(targetObj, serviceObject);
            }
        }
    }

    private boolean isImplementationClass(Class<?> serviceInterface, Class<?> serviceImpl) {
        return !Modifier.isAbstract(serviceImpl.getModifiers()) && serviceInterface.isAssignableFrom(serviceImpl);
    }

}
