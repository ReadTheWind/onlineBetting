package com.onlinebetting.web;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class WebContainerConstructor {

    public HttpServer initialize(int port) throws IOException {
        return HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void setExecutor(Executor executor, HttpServer httpServer) {
        httpServer.setExecutor(executor);
    }

    public void createContext(HttpServer httpServer, String path, HttpHandler handler) {
        httpServer.createContext(path, handler);
    }
}
