package com.onlinebetting.web;

import com.onlinebetting.web.constants.WebConstant;
import com.onlinebetting.web.enums.MethodType;
import com.onlinebetting.web.exception.PathMatchExecutorException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public class WebHttpHandler implements HttpHandler {

    private final Dispatcher dispatcher;

    public WebHttpHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String requestPath = exchange.getRequestURI().getPath();
        String requestParameters = exchange.getRequestURI().getQuery();

        MethodType methodType = MethodType.getMethodType(exchange.getRequestMethod());
        OutputStream httpResponse = exchange.getResponseBody();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String requestBody = reader.readLine();
            Object response = dispatcher.dispatch(requestPath, requestParameters, requestBody, methodType);

            exchange.sendResponseHeaders(WebConstant.SUCCESS_HTTP_STATUS_CODE, WebConstant.INT_ZERO);
            httpResponse.write((null == response ? WebConstant.EMPTY_STRING : response.toString()).getBytes());
        } catch (PathMatchExecutorException e) {
            e.printStackTrace();

            exchange.sendResponseHeaders(WebConstant.PATH_NOT_FOUND_HTTP_STATUS_CODE, WebConstant.INT_ZERO);
        } catch (Exception e) {
            e.printStackTrace();

            exchange.sendResponseHeaders(WebConstant.ERROR_HTTP_STATUS_CODE, WebConstant.INT_ZERO);
            try {
                httpResponse.write(e.getMessage().getBytes());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } finally {
            httpResponse.close();
        }

    }
}
