package com.onlinebetting.pojo.vo;

import com.onlinebetting.constant.Constant;

public class BetOfferDetail {

    private long customerId;

    private double stakeAmount;

    private BetOfferDetail(long customerId, double stakeAmount) {
        this.customerId = customerId;
        this.stakeAmount = stakeAmount;
    }

    public static BetOfferDetail of(long customerId, double stakeAmount) {
        return new BetOfferDetail(customerId, stakeAmount);
    }

    public Long getCustomerId() {
        return customerId;
    }


    public double getStakeAmount() {
        return stakeAmount;
    }


    @Override
    public String toString() {
        return customerId + Constant.EQUAL_SIGN + stakeAmount;
    }
}
