package com.onlinebetting.web.bean;

import com.onlinebetting.web.annotation.Service;
import com.onlinebetting.web.annotation.WebController;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class BeanScanner {

    public static Set<String> scanServiceBean(String rootPackage) {
        try {
            return getAnnotatedClasses(rootPackage, Service.class);
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> scanControllerBean(String packageName) {
        try {
            return getAnnotatedClasses(packageName, WebController.class);
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Set<Field> getAnnotatedFields(Class<?> clazz, Class<?> fieldAnnotations) {
        Set<Field> fields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if(field.isAnnotationPresent((Class<? extends Annotation>) fieldAnnotations)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public static Set<Method> getAnnotatedMethods(Class<?> clazz, Class<?> methodAnnotations) {
        Set<Method> methods = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (isMethodAnnotated(method, methodAnnotations)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private static boolean isMethodAnnotated(Method method, Class<?>... methodAnnotations) {
        for (Class<?> methodAnnotation : methodAnnotations) {
            if (method.isAnnotationPresent((Class<? extends Annotation>) methodAnnotation)) return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static Set<String> getAnnotatedClasses(String packageName, Class<?>... annotations) throws URISyntaxException, ClassNotFoundException {

        File folder = new File(Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/")).toURI());
        Set<String> beanNames = new HashSet<>();
        for (File file : folder.listFiles()) {

            if (isClassFile(file)) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().lastIndexOf("."));
                Class<?> clazz = Class.forName(className);

                if (isClassAnnotated(clazz, annotations)) beanNames.add(className);
            } else if(file.isDirectory()) {
                String subordinatePackageName = packageName + "." + file.getName();
                beanNames.addAll(getAnnotatedClasses(subordinatePackageName, annotations));
            }
        }
        return beanNames;
    }

    private static boolean isClassAnnotated(Class<?> clazz, Class<?>... annotations) {
        for (Class<?> annotation : annotations) {
            if(clazz.isAnnotationPresent((Class<? extends Annotation>) annotation)) return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static boolean isClassFile(File file) {
        return file.isFile() && file.getName().endsWith(".class");
    }
}
