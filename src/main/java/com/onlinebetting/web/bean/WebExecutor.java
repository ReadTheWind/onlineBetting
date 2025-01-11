package com.onlinebetting.web.bean;

import com.onlinebetting.web.ParameterConverterFactory;
import com.onlinebetting.web.annotation.PathParameter;
import com.onlinebetting.web.annotation.RequestBody;
import com.onlinebetting.web.annotation.RequestParam;
import com.onlinebetting.web.constants.WebConstant;
import com.onlinebetting.web.converter.ParameterConverter;
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

    private ParameterConverterFactory parameterConverterFactory = new ParameterConverterFactory();

    public WebExecutor(String controllerName, String controllerPath, String methodPath, Method method, MethodType methodType) {
        this.controllerName = controllerName;
        this.controllerPath = controllerPath;
        this.methodPath = methodPath;
        this.method = method;
        this.methodType = methodType;
        initializeParameters();
        parameterConverterFactory.initConverters();
    }

    public Object invoke() {
        try {
            Object controllerObject = SingletonBeanFactory.getBean(controllerName);
            return method.invoke(controllerObject, buildParameterArray());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
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
        ParameterConverter converter = parameterConverterFactory.getConverter(parameter);
        return converter.convert(parameterValue);
    }

    private void initializeParameters() {
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            if (parameter.isAnnotationPresent(PathParameter.class)) {
                PathParameter pathParameter = method.getParameters()[i].getAnnotation(PathParameter.class);
                parameters.put(pathParameter.name(), WebConstant.EMPTY_STRING);
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = method.getParameters()[i].getAnnotation(RequestParam.class);
                parameters.put(requestParam.name(), WebConstant.EMPTY_STRING);
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
