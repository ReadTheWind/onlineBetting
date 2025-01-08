package com.onlinebetting.controller;

import com.onlinebetting.service.SessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {



    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/{customerId}/session")
    public String getSession(@PathVariable("customerId") long customerId) {
        return sessionService.getSession(customerId);
    }

}
