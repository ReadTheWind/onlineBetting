package com.onlinebetting.web.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonBeanFactory {

    private SingletonBeanFactory(){}

    private final static Map<String, Object> INSTANCE_CONTAINER = new ConcurrentHashMap<>();

    public static Object getBean(String className) {
        synchronized (INSTANCE_CONTAINER) {
            if (!INSTANCE_CONTAINER.containsKey(className)) {
                Object obj = null;
                try {
                    obj = Class.forName(className).getDeclaredConstructor().newInstance();
                    INSTANCE_CONTAINER.put(className, obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return obj;
            } else {
                return INSTANCE_CONTAINER.get(className);
            }
        }
    }
}
