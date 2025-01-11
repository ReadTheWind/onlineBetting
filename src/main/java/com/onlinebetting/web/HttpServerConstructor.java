package com.onlinebetting.web;

import com.onlinebetting.web.constants.WebConstant;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(WebConstant.RESOURCE_FILE_NAME);
            prop.load(Files.newInputStream(Paths.get(url.toURI())));
            return Integer.parseInt((String) prop.get(WebConstant.SERVER_PORT_KEY));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Read property file error...");
        }
    }
}
