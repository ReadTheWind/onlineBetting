package com.onlinebetting.web;

import com.onlinebetting.web.constants.WebConstant;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public final class HttpServerConstructor {

    private final HttpServer httpServer;

    public HttpServerConstructor(Executor executor) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(getHttpPort()), 0);
        this.httpServer.setExecutor(executor);
    }

    public HttpServer createContext(String path, HttpHandler handler) {
        this.httpServer.createContext(path, handler);
        return this.httpServer;
    }

    private int getHttpPort() {
        return WebConstant.SERVER_PORT;
    }
}
