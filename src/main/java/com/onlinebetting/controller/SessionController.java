package com.onlinebetting.controller;

import com.onlinebetting.service.SessionService;
import com.onlinebetting.web.annotation.Autowired;
import com.onlinebetting.web.annotation.PathParameter;
import com.onlinebetting.web.annotation.RequestMapping;
import com.onlinebetting.web.annotation.WebController;
import com.onlinebetting.web.enums.MethodType;

@WebController(name = "sessionController")
public class SessionController {


    @Autowired
    public SessionService sessionService;

    @RequestMapping(path = "/{customerId}/session", method = MethodType.GET)
    public String getSession(@PathParameter(name = "customerId") long customerId) {
        return sessionService.getSession(customerId);
    }

}
