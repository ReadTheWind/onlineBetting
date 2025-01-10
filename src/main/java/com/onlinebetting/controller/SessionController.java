package com.onlinebetting.controller;

import com.onlinebetting.service.SessionService;
import com.onlinebetting.web.enums.MethodType;
import com.onlinebetting.web.annotation.*;

@WebController(name = "sessionController")
public class SessionController {


    @Autowired(name = "sessionService")
    public SessionService sessionService;

    @RequestMapping(path = "/{customerId}/session", method = MethodType.GET)
    public String getSession(@PathParameter(name = "customerId") long customerId) {
        return sessionService.getSession(customerId);
    }

}
