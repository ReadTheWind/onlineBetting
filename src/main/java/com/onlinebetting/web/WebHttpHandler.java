package com.onlinebetting.web;

import com.onlinebetting.web.enums.MethodType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class WebHttpHandler implements HttpHandler {

    private final Dispatcher dispatcher;

    public WebHttpHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();
        String realPath = exchange.getRequestURI().getPath();
        String parameters = exchange.getRequestURI().getQuery();

        String requestBody = "";
        char[] buffer = new char[1024];
        int len;
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        while ((len = inputStreamReader.read(buffer)) != -1) {
            requestBody = new String(buffer, 0, len);
        }
        MethodType methodType = MethodType.getMethodType(exchange.getRequestMethod());

        OutputStream httpResponse = exchange.getResponseBody();
        try {
            Object response = dispatcher.dispatch(realPath, parameters, requestBody, methodType);

            exchange.sendResponseHeaders(200, 0);
            httpResponse.write(response.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
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
