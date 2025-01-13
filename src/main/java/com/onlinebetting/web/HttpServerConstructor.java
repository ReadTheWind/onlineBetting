package com.onlinebetting.web;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executor;

public final class HttpServerConstructor {

    private HttpServer httpServer;
    private final Properties prop = new Properties();

    public HttpServerConstructor(Executor executor) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(getHttpPort()), 0);
        this.httpServer.setExecutor(executor);
    }

    public HttpServer createContext(String path, HttpHandler handler) {
        this.httpServer.createContext(path, handler);
        return this.httpServer;
    }

    private int getHttpPort() {
        return 8001;
    }
}
