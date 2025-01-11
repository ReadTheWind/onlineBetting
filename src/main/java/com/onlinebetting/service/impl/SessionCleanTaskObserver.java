package com.onlinebetting.service.impl;

import com.onlinebetting.service.SessionService;
import com.onlinebetting.web.annotation.Autowired;
import com.onlinebetting.web.annotation.Service;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service(name = "sessionCleanTaskObserver")
public class SessionCleanTaskObserver implements Observer {

    @Autowired(name = "sessionService")
    private SessionService sessionService;

    @Override
    public void update(Observable o, Object arg) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> sessionService.cleanUpInvalidSession(), 1, 1, TimeUnit.MINUTES);
        System.out.println("remove  expired session task started！");
    }


}
