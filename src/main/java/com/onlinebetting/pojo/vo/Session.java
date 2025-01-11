package com.onlinebetting.pojo.vo;

import com.onlinebetting.utils.AESUtil;
import com.onlinebetting.web.constants.WebConstant;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Session {

    private final static String SPLICING_SYMBOL = "#";

    private Long customerId;

    private Long timestamp;

    public Session(Long customerId, Long timestamp) {
        this.customerId = customerId;
        this.timestamp = timestamp;
    }

    public static Session of(long customerId, LocalDateTime time) {
        long second = time.toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(time));
        return new Session(customerId, second);
    }

    public String getSessionKey() {
        try {
            return AESUtil.encrypt(this.toString());
        } catch (Exception e) {
            throw new RuntimeException("session encrypt error!");
        }
    }

    public static Session parseSession(String session) {
        try {
            String originalSession = AESUtil.decrypt(session);
            String[] split = originalSession.split(SPLICING_SYMBOL);
            Long customerId = Long.valueOf(split[0]);
            Long createTimestamp = Long.valueOf(split[1]);
            return new Session(customerId, createTimestamp);
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal Session !");
        }
    }

    public boolean isExpired() {
        Long createTimestamp = getTimestamp();
        LocalDateTime now = LocalDateTime.now();
        Long second = now.plusMinutes(-10).toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(now));
        return createTimestamp.compareTo(second) <= WebConstant.INT_ZERO;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return customerId.toString().concat(SPLICING_SYMBOL).concat(timestamp.toString());
    }
}
