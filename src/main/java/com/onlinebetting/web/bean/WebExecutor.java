package com.onlinebetting.web.bean;

import com.onlinebetting.web.annotation.PathParameter;
import com.onlinebetting.web.annotation.RequestBody;
import com.onlinebetting.web.annotation.RequestParam;
import com.onlinebetting.web.enums.MethodType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebExecutor {

    private final String controllerName;
    private final String controllerPath;
    private final Method method;
    private final String methodPath;
    private final MethodType methodType;

    private String requestBodyName;
    private String requestBodyValue;

    private final Map<String, String> pathParameters = new ConcurrentHashMap<>();

    private final Map<String, String> parameters = new ConcurrentHashMap<>();

    public WebExecutor(String controllerName, String controllerPath, String methodPath, Method method, MethodType methodType) {
        this.controllerName = controllerName;
        this.controllerPath = controllerPath;
        this.methodPath = methodPath;
        this.method = method;
        this.methodType = methodType;
        initializeParameters();
    }

    public Object invoke() {
        try {
            Object controllerObject = SingletonBeanFactory.getBean(controllerName);
            return method.invoke(controllerObject, buildParameterArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] buildParameterArray() {
        Object[] paramValues = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            if (null == parameter) {
                continue;
            }
            if (parameter.isAnnotationPresent(PathParameter.class)) {
                PathParameter pathParameter = method.getParameters()[i].getAnnotation(PathParameter.class);
                paramValues[i] = covertParameterType(parameter, pathParameters.get(pathParameter.name()));
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = method.getParameters()[i].getAnnotation(RequestParam.class);
                paramValues[i] = covertParameterType(parameter, parameters.get(requestParam.name()));
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                this.requestBodyName = method.getParameters()[i].getName();
                paramValues[i] = covertParameterType(parameter, this.requestBodyValue);
            }
        }
        return paramValues;
    }

    private Object covertParameterType(Parameter parameter, String parameterValue) {
        switch (parameter.getParameterizedType().getTypeName()) {
            case "java.lang.String":
                return parameterValue;
            case "Long":
                return "".equals(parameterValue) ? null : Long.parseLong(parameterValue);
            case "long":
                return "".equals(parameterValue) ? 0L : Long.parseLong(parameterValue);
            case "int":
                return "".equals(parameterValue) ? 0 : Integer.parseInt(parameterValue);
            case "Integer":
                return "".equals(parameterValue) ? null : Integer.parseInt(parameterValue);
            case "double":
                return "".equals(parameterValue) ? 0d : Double.parseDouble(parameterValue);
            case "Double":
                return "".equals(parameterValue) ? null : Double.parseDouble(parameterValue);
            default:
                throw new IllegalArgumentException();
        }
    }


    private void initializeParameters() {
        for (int i = 0; i < method.getParameterCount(); i++) {

            Parameter parameter = method.getParameters()[i];
            if (parameter.isAnnotationPresent(PathParameter.class)) {
                PathParameter pathParameter = method.getParameters()[i].getAnnotation(PathParameter.class);
                parameters.put(pathParameter.name(), "");
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = method.getParameters()[i].getAnnotation(RequestParam.class);
                parameters.put(requestParam.name(), "");
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                this.requestBodyName = method.getParameters()[i].getName();
            }
        }
    }

    public void setPathParameterValue(String pathParameterName, String pathParameterValue) {
        pathParameters.put(pathParameterName, pathParameterValue);
    }

    public void setParameterValue(String parameterName, String value) {
        parameters.put(parameterName, value);
    }

    public void setRequestBodyValue(String value) {
        this.requestBodyValue = value;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public String getMethodPath() {
        return methodPath;
    }
}
