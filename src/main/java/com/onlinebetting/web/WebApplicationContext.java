package com.onlinebetting.web;

import com.onlinebetting.service.impl.SessionCommandLineRunner;
import com.onlinebetting.web.bean.BeanLoader;
import com.onlinebetting.web.bean.SingletonBeanFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class WebApplicationContext extends Observable {

    private final Package rootPackage;
    private final HttpContainerFactory factory = new HttpContainerFactory();
    private final BeanLoader loader = new BeanLoader();

    private WebApplicationContext(Package rootPackage){
        this.rootPackage = rootPackage;
    }

    private void run() throws IOException, IllegalAccessException {
        loader.loadAllBean(rootPackage.getName());
        Dispatcher dispatcher = new Dispatcher(loader.getExecutors());
        HttpServer server = factory.prepareWebProcessor(dispatcher);
        server.start();
        System.out.println("web server stared!");
        setChanged();
        notifyObservers();
    }

    public static void start(Class<?> primarySource, String... args) throws IOException, IllegalAccessException {
        WebApplicationContext webApplicationContext = new WebApplicationContext(primarySource.getPackage());
        webApplicationContext.addObserver((Observer) SingletonBeanFactory.getBean(SessionCommandLineRunner.class.getName()));
        webApplicationContext.run();
    }
}
