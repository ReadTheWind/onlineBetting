package com.onlinebetting.web;

import com.onlinebetting.utils.PropertyUtil;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Runtime.getRuntime;

public class HttpContainerFactory {

    private static final String ROOT_PATH = "/";
    private final Executor worker;
    private static final int WORK_QUEUE_CAPACITY = 1000;
    private final WebContainerConstructor webContainerConstructor = new WebContainerConstructor();

    public HttpContainerFactory() {
        worker = new ThreadPoolExecutor(
            getRuntime().availableProcessors() * 2,
            getRuntime().availableProcessors() * 10,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(WORK_QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public HttpServer prepareWebProcessor(Dispatcher dispatcher) throws IOException {
        int webPort = Integer.parseInt(PropertyUtil.getByKey("server.port"));
        HttpServer httpServer = webContainerConstructor.initialize(webPort);
        webContainerConstructor.setExecutor(worker, httpServer);
        webContainerConstructor.createContext(httpServer, ROOT_PATH, new WebHttpHandler(dispatcher));
        return httpServer;
    }

}
