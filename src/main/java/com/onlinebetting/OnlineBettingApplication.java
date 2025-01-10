package com.onlinebetting;

import com.onlinebetting.web.WebApplicationContext;

import java.io.IOException;

public class OnlineBettingApplication {

    public static void main(String[] args) throws IOException, IllegalAccessException {
        WebApplicationContext.start(OnlineBettingApplication.class, args);
    }
}
