package com.onlinebetting.web;

import com.onlinebetting.web.bean.WebExecutor;
import com.onlinebetting.web.enums.MethodType;

import java.util.Map;
import java.util.Optional;

public class Dispatcher {

    private final Map<String, WebExecutor> executors;

    public Dispatcher(Map<String, WebExecutor> executors) {
        this.executors = executors;
    }

    public Object dispatch(String realPath, String parameters, String requestBody, MethodType methodType) {

        WebExecutor executor = initWebExecutor(realPath, parameters, requestBody, methodType);

        return invoke(executor);
    }

    private Object invoke(WebExecutor executor) {
        return executor.invoke();
    }

    private WebExecutor initWebExecutor(String realPath, String parameters, String requestBody, MethodType methodType) {

        WebExecutor executor = getWebExecutor(realPath, methodType);

        if (null == executor) {
            throw new NullPointerException("could not match any server paths...");
        }

        processPathParameters(realPath, executor);

        processParameters(parameters, requestBody, executor);

        return executor;
    }

    private WebExecutor getWebExecutor(String realPath, MethodType methodType) {
        Optional<Map.Entry<String, WebExecutor>> matchExecutor = executors.entrySet().stream().filter(i -> this.isMatch(i, realPath, methodType)).findFirst();
        return matchExecutor.map(Map.Entry::getValue).orElse(null);
    }

    private void processParameters(String parameters, String requestBody, WebExecutor executor) {

        if (parameters == null || parameters.length() < 1) return;

        if (parameters.contains("&")) {
            String[] parameterArray = parameters.split("&");
            for (String str : parameterArray) {
                String[] parameter = str.split("=");
                executor.setParameterValue(parameter[0], parameter[1]);
            }
        } else {
            String[] parameter = parameters.split("=");
            executor.setParameterValue(parameter[0], parameter[1]);
        }
        executor.setRequestBodyValue(requestBody);
    }

    private void processPathParameters(String realPath, WebExecutor executor) {

        String[] serverPathItem = executor.getMethodPath().split("/");
        String[] clientPathItem = realPath.split("/");

        for (int i = 1; i < serverPathItem.length; i++) {
            if (serverPathItem[i].startsWith("{") && serverPathItem[i].endsWith("}")) {
                String pathParameterValue = clientPathItem[i];
                String parameterName = serverPathItem[i].substring(1, serverPathItem[i].length() - 1);
                executor.setPathParameterValue(parameterName, pathParameterValue);
            }
        }
    }

    private boolean isMatch(Map.Entry<String, WebExecutor> entry, String realPath, MethodType methodType) {

        boolean isMatch = Boolean.TRUE;

        String[] serverPathItem = entry.getKey().split("/");
        String[] clientPathItem = realPath.split("/");

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
        return serverPathItem.startsWith("{") && serverPathItem.endsWith("}");
    }

}
