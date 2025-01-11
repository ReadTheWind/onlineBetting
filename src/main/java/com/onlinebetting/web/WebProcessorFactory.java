package com.onlinebetting.web;

import com.onlinebetting.web.constants.WebConstant;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Runtime.getRuntime;

public class WebProcessorFactory {

    private static final String ROOT_PATH = WebConstant.SLASH;
    private final Executor worker;
    private static final int WORK_QUEUE_CAPACITY = 1000;

    public WebProcessorFactory() {
        worker = new ThreadPoolExecutor(
                getRuntime().availableProcessors() * 5,
                getRuntime().availableProcessors() * 20,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(WORK_QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public HttpServer buildWebProcessor(Dispatcher dispatcher) throws IOException {
        HttpServerConstructor httpServerConstructor = new HttpServerConstructor(worker);
        return httpServerConstructor.createContext(ROOT_PATH, new WebHttpHandler(dispatcher));
    }

}
