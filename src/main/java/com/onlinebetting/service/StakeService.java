package com.onlinebetting.service;

public interface StakeService {

    void offerStake(long betOfferId, String sessionKey, double amount) throws Exception;

    String highStakes(long betOfferId);

}
