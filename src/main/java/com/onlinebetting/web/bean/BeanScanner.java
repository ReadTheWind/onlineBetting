package com.onlinebetting.web.bean;

import com.onlinebetting.web.annotation.Service;
import com.onlinebetting.web.annotation.WebController;
import com.onlinebetting.web.constants.WebConstant;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BeanScanner {

    public static Set<String> scanServiceBeanClassNames(String rootPackage) {
        try {
            return getAnnotatedClasses(rootPackage, Service.class);
        } catch (Exception e) {
            throw new RuntimeException("load service bean fail ...", e);
        }
    }

    public static Set<String> scanControllerBeanClassNames(String packageName) {
        try {
            return getAnnotatedClasses(packageName, WebController.class);
        } catch (Exception e) {
            throw new RuntimeException("load controller bean fail ...", e);
        }
    }

    public static Set<Field> getAnnotatedFields(Class<?> clazz, Class<?> fieldAnnotations) {
        Set<Field> fields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent((Class<? extends Annotation>) fieldAnnotations)) {
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

    private static Set<String> getAnnotatedClasses(String packageName, Class<?>... annotations) throws ClassNotFoundException, IOException {
        Set<String> classNames = new HashSet<>();
        String decode = URLDecoder.decode(packageName.replace(WebConstant.DOT, WebConstant.SLASH), WebConstant.UTF_8);
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(decode);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals(WebConstant.FILE)) {
                parseFileResourceClasses(packageName, classNames, new File(URLDecoder.decode(resource.getFile(), WebConstant.UTF_8)), annotations);
            } else if (resource.getProtocol().equals(WebConstant.JAR)) {
                parseJarResourceClasses(packageName, classNames, resource, annotations);
            }
        }

        return classNames;
    }

    private static void parseFileResourceClasses(String packageName, Set<String> classNames, File fileResource, Class<?>... annotations) throws ClassNotFoundException {
        File[] files = fileResource.listFiles();
        if (null == files) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                String filePackageName = packageName.concat(WebConstant.DOT).concat(file.getName());
                parseFileResourceClasses(filePackageName, classNames, file, annotations);
            } else if (isClassFile(file)) {
                String className = buildFileClassName(packageName, file);
                Class<?> clazz = Class.forName(className);
                if (!isClassAnnotated(clazz, annotations)) {
                    continue;
                }

                classNames.add(className);
            }
        }
    }

    private static void parseJarResourceClasses(String packageName, Set<String> classNames, URL resource, Class<?>[] annotations) throws IOException, ClassNotFoundException {
        JarURLConnection jarURLconnection = (JarURLConnection) resource.openConnection();
        JarFile jarFile = jarURLconnection.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String jarFileName = jarEntry.getName().replace(WebConstant.SLASH, WebConstant.DOT);

            if (!jarFileName.startsWith(packageName) || !jarFileName.endsWith(WebConstant.CLASS_SUFFIX)) {
                continue;
            }

            String className = removeClassSuffix(jarFileName);
            Class<?> clazz = Class.forName(className);
            if (!isClassAnnotated(clazz, annotations)) {
                continue;
            }

            classNames.add(className);
        }
    }

    private static String buildFileClassName(String packageName, File f) {
        return packageName + WebConstant.DOT + removeClassSuffix(f.getName());
    }

    private static String removeClassSuffix(String className) {
        return className.substring(0, className.lastIndexOf(WebConstant.DOT));
    }

    private static boolean isClassAnnotated(Class<?> clazz, Class<?>... annotations) {
        for (Class<?> annotation : annotations) {
            if (clazz.isAnnotationPresent((Class<? extends Annotation>) annotation)) return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static boolean isClassFile(File file) {
        return file.isFile() && file.getName().endsWith(WebConstant.CLASS_SUFFIX);
    }
}
