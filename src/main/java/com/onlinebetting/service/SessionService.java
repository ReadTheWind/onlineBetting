package com.onlinebetting.service;

public interface SessionService {

    String getSession(long customerId);

    long validateSession(String sessionKey) throws Exception;

    void cleanUpInvalidSession();

}
