package com.onlinebetting.web;

import com.onlinebetting.web.annotation.RequestMapping;
import com.onlinebetting.web.annotation.WebController;
import com.onlinebetting.web.bean.BeanScanner;
import com.onlinebetting.web.bean.WebExecutor;
import com.onlinebetting.web.constants.WebConstant;
import com.onlinebetting.web.enums.MethodType;
import com.onlinebetting.web.exception.PathMatchExecutorFailException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {

    public final Map<String, WebExecutor> executors = new ConcurrentHashMap<>();

    public Dispatcher(List<Object> controllerBeans) {
        handleMapping(controllerBeans);
    }

    private void handleMapping(List<Object> controllerBeans) {
        for (Object controllerBean : controllerBeans) {
            handleControllerMapping(controllerBean);
        }
    }

    private void handleControllerMapping(Object controllerBean) {
        Class<?> controllerClazz = controllerBean.getClass();
        WebController webController = controllerClazz.getAnnotation(WebController.class);
        validatePath(webController.path());
        handleMethodMapping(controllerBean, webController.path());
    }

    private void handleMethodMapping(Object controllerBean, String path) {

        Class<?> controllerClazz = controllerBean.getClass();
        Set<Method> methods = BeanScanner.getAnnotatedMethods(controllerClazz, RequestMapping.class);
        for (Method method : methods) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            validatePath(requestMapping.path());
            buildExecutorByMethod(controllerClazz, path, requestMapping, method);
        }
    }

    private void buildExecutorByMethod(Class<?> controllerClazz, String path, RequestMapping requestMapping, Method method) {
        String fullPath = path + requestMapping.path();
        WebExecutor executor = new WebExecutor(controllerClazz.getName(), path, requestMapping.path(), method, requestMapping.method());
        executors.put(fullPath, executor);
    }


    public Object dispatch(String requestPath, String parameters, String requestBody, MethodType methodType) {

        WebExecutor executor = initWebExecutor(requestPath, parameters, requestBody, methodType);

        return invoke(executor);
    }

    private Object invoke(WebExecutor executor) {
        return executor.invoke();
    }

    private WebExecutor initWebExecutor(String requestPath, String parameters, String requestBody, MethodType methodType) {

        WebExecutor executor = findWebExecutorByRequestPath(requestPath, methodType);

        if (null == executor) throw new PathMatchExecutorFailException();

        processPathParameters(requestPath, executor);

        processParameters(parameters, requestBody, executor);

        return executor;
    }

    private WebExecutor findWebExecutorByRequestPath(String realPath, MethodType methodType) {
        Optional<Map.Entry<String, WebExecutor>> matchExecutor = executors.entrySet().stream().filter(i -> this.isMatch(i, realPath, methodType)).findFirst();
        return matchExecutor.map(Map.Entry::getValue).orElse(null);
    }


    private void validatePath(String path) {
        if (path != null && !path.equals(WebConstant.EMPTY_STRING) && !path.startsWith(WebConstant.SLASH))
            throw new IllegalArgumentException("Path is illegal... Please start with '/'");
    }

    private void processParameters(String parameters, String requestBody, WebExecutor executor) {

        if (isEmpty(parameters)) return;

        if (isMultipleParameter(parameters)) {
            processMultipleParameter(parameters, executor);
        } else {
            processSingleParameter(parameters, executor);
        }
        executor.setRequestBodyValue(requestBody);
    }

    private boolean isEmpty(String parameters) {
        return parameters == null || parameters.length() < 1;
    }

    private boolean isMultipleParameter(String parameters) {
        return parameters.contains(WebConstant.AND_SIGN);
    }

    private void processSingleParameter(String parameters, WebExecutor executor) {
        executor.setParameterValue(getSingleParameterKey(parameters), getSingleParameterValue(parameters));
    }

    private String getSingleParameterKey(String parameters) {
        return parameters.substring(0, parameters.indexOf(WebConstant.EQUALS_SIGN));
    }

    private String getSingleParameterValue(String parameters) {
        return parameters.substring(parameters.indexOf(WebConstant.EQUALS_SIGN) + 1);
    }

    private void processMultipleParameter(String parameters, WebExecutor executor) {
        String[] parameterArray = parameters.split(WebConstant.AND_SIGN);
        for (String parameter : parameterArray) {
            processSingleParameter(parameter, executor);
        }
    }

    private void processPathParameters(String realPath, WebExecutor executor) {

        String[] serverPathItem = executor.getMethodPath().split(WebConstant.SLASH);
        String[] clientPathItem = realPath.split(WebConstant.SLASH);

        for (int i = 1; i < serverPathItem.length; i++) {
            if (serverPathItem[i].startsWith(WebConstant.OPEN_BRACE) && serverPathItem[i].endsWith(WebConstant.CLOSE_BRACE)) {
                String pathParameterValue = clientPathItem[i];
                String parameterName = serverPathItem[i].substring(1, serverPathItem[i].length() - 1);
                executor.setPathParameterValue(parameterName, pathParameterValue);
            }
        }
    }

    private boolean isMatch(Map.Entry<String, WebExecutor> entry, String realPath, MethodType methodType) {

        boolean isMatch = Boolean.TRUE;

        String[] serverPathItem = entry.getKey().split(WebConstant.SLASH);
        String[] clientPathItem = realPath.split(WebConstant.SLASH);

        for (int i = 1; i < serverPathItem.length; i++) {
            if (!isPathRequest(serverPathItem[i]) && !serverPathItem[i].equals(clientPathItem[i])) {
                isMatch = false;
                break;
            }
        }

        if (isMatch && !entry.getValue().getMethodType().equals(methodType)) {
            isMatch = false;
        }

        return isMatch;
    }

    private boolean isPathRequest(String serverPathItem) {
        return serverPathItem.startsWith(WebConstant.OPEN_BRACE) && serverPathItem.endsWith(WebConstant.CLOSE_BRACE);
    }

}
