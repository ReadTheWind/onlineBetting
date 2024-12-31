package com.onlinebetting.service.impl;

import com.onlinebetting.pojo.vo.Customers;
import com.onlinebetting.pojo.vo.Session;
import com.onlinebetting.service.SessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionServiceImpl implements SessionService {

    private final Map<Long, String> sessions = new ConcurrentHashMap<>();

    private final Map<Integer, Customers> sessionRecorder = new ConcurrentHashMap<>();

    @Override
    public String getSession(long customerId) {

        LocalDateTime time = LocalDateTime.now();

        String session = sessions.computeIfPresent(customerId, (customer, sessionKey) -> createSessionIfPresent(customer, sessionKey, time));

        if (null != session && session.length() > 0) {
            return session;
        }

        return sessions.computeIfAbsent(customerId, customer -> createSessionIfAbsent(customer, time));
    }

    private String createSessionIfAbsent(long customerId, LocalDateTime time) {
        return generateSession(customerId, time);
    }

    private String createSessionIfPresent(long customerId, String sessionKey, LocalDateTime time) {
        Session session = Session.parseSession(sessionKey);
        if (session.isExpired()) {
            return generateSession(customerId, time);
        }
        return sessionKey;
    }

    private String generateSession(long customerId, LocalDateTime time) {
        String sessionKey = Session.of(customerId, time).getSessionKey();

        int minute = getTimeMinute(time);
        sessionRecorder.computeIfAbsent(minute, customerIds -> new Customers());
        sessionRecorder.computeIfPresent(minute, (k, customers) -> customers.addCustomerId(customerId));
        return sessionKey;
    }

    private int getTimeMinute(LocalDateTime now) {
        return now.getMinute();
    }

    private int getPastElevenMinute() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.minus(11, ChronoUnit.MINUTES).getMinute();
    }

    @Override
    public long validateSession(String sessionKey) {
        Session session = Session.parseSession(sessionKey);
        if (session.isExpired()) {
            throw new IllegalArgumentException("the current session is expired...");
        }
        return session.getCustomerId();
    }

    @Override
    public void cleanUpInvalidSession() {

        int minute = getPastElevenMinute();

        sessionRecorder.computeIfPresent(minute, this::sweep);
    }

    private Customers sweep(int minute, Customers customers) {

        if (customers.getCustomerIds().isEmpty()) {
            return customers;
        }

        sweepSession(customers.getCustomerIds());

        customers.clear();

        return customers;
    }

    private void sweepSession(Set<Long> customerIds) {
        customerIds.forEach(sessions::remove);
    }
}
