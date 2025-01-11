package com.onlinebetting.web;

import com.onlinebetting.service.impl.SessionCleanTaskObserver;
import com.onlinebetting.web.bean.BeanLoader;
import com.onlinebetting.web.bean.SingletonBeanFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class WebApplicationContext extends Observable {

    private final Package rootPackage;
    private final WebProcessorFactory webProcessorFactory = new WebProcessorFactory();
    private final BeanLoader beanLoader = new BeanLoader();

    private WebApplicationContext(Package rootPackage){
        this.rootPackage = rootPackage;
    }

    private void run() throws IOException, IllegalAccessException {

        List<Object> serviceBeans = beanLoader.loadServiceBean(rootPackage.getName());
        List<Object> controllerBeans = beanLoader.loadControllerBean(rootPackage.getName(), serviceBeans);

        Dispatcher dispatcher = new Dispatcher(controllerBeans);
        HttpServer httpServer = webProcessorFactory.buildWebProcessor(dispatcher);
        httpServer.start();
        System.out.println("web server stared!");

        setChanged();
        notifyObservers();
    }

    public static void start(Class<?> primarySource, String... args) throws IOException, IllegalAccessException {
        WebApplicationContext webApplicationContext = new WebApplicationContext(primarySource.getPackage());
        webApplicationContext.addObserver((Observer) SingletonBeanFactory.getBean(SessionCleanTaskObserver.class.getName()));
        webApplicationContext.run();
    }
}
