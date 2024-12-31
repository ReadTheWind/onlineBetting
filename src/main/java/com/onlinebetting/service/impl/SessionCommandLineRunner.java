package com.onlinebetting.service.impl;

import com.onlinebetting.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SessionCommandLineRunner implements CommandLineRunner {

    @Autowired
    private SessionService sessionService;

    @Override
    public void run(String... args) {
        System.out.println("remove  expired session task started！");

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> sessionService.cleanUpInvalidSession(), 1, 1, TimeUnit.MINUTES);

    }

}
