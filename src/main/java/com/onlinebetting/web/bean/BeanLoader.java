package com.onlinebetting.web.bean;

import com.onlinebetting.web.annotation.Autowired;
import com.onlinebetting.web.annotation.RequestMapping;
import com.onlinebetting.web.annotation.Service;
import com.onlinebetting.web.annotation.WebController;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BeanLoader {

    public final Map<String,WebExecutor> executors = new ConcurrentHashMap<>();

    public void loadAllBean(String packageName) throws IllegalAccessException {

        Set<String> controllerBeanNames = BeanScanner.scanControllerBean(packageName);
        Set<String> services = BeanScanner.scanServiceBean(packageName);

        List<Object> serviceObjs = services.stream().map(SingletonBeanFactory::getBean).collect(Collectors.toList());

        for (Object serviceObj : serviceObjs) {
            setField(serviceObj.getClass(), serviceObj, serviceObjs);
        }

        for (String controllerBeanName : controllerBeanNames) {

            Object controllerObj = SingletonBeanFactory.getBean(controllerBeanName);
            Class<?> controllerClazz = controllerObj.getClass();
            WebController webController = controllerClazz.getAnnotation(WebController.class);
            validatePath(webController.path());

            Set<Method> methods = BeanScanner.getAnnotatedMethods(controllerClazz, RequestMapping.class);
            for (Method method : methods) {

                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                validatePath(requestMapping.path());

                String fullPath = webController.path() + requestMapping.path();
                WebExecutor executor = new WebExecutor(controllerBeanName, webController.path(), requestMapping.path(), method, requestMapping.method());
                executors.put(fullPath, executor);
            }
            setField(controllerClazz, controllerObj, serviceObjs);
        }
    }

    private void setField(Class<?> clazz, Object targetObj, List<Object> serviceObjs) throws IllegalAccessException {

        Set<Field> fields = BeanScanner.getAnnotatedFields(clazz, Autowired.class);

        for (Field field : fields) {

            if(!field.getType().isInterface()) {
                Object fieldObj = SingletonBeanFactory.getBean(field.getType().getName());
                Class<?> fieldClass = fieldObj.getClass();
                if(!fieldClass.isAnnotationPresent(Service.class)) throw new IllegalStateException("Please declare the class annotation...");

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

    public Map<String, WebExecutor> getExecutors() {
        return executors;
    }

    private void validatePath(String path) {
        if(path != null && !path.equals("") && !path.startsWith("/")) throw new IllegalArgumentException("path is illegal... Please start with '/'");
    }
}
