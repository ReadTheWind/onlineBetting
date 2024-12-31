package com.onlinebetting;

import com.onlinebetting.service.SessionService;
import com.onlinebetting.service.StakeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
class OnlineBettingTests {

    @Autowired
    private SessionService sessionService;
    @Autowired
    private StakeService stakeService;

    @Test
    public void singleThreadOnlineBettingTest() throws Exception {
        long bitOfferId = 1;
        for (int i = 1; i <= 500; i++) {
            long customerId = i / 10;
            String session = sessionService.getSession(customerId);
            stakeService.offerStake(bitOfferId, session, i);
        }
        String s = stakeService.highStakes(bitOfferId);
        System.out.println(s);
    }

    @Test
    public void multiThreadOnlineBettingTest() throws Exception {
        long bitOfferId = 1;
        CountDownLatch cdl = new CountDownLatch(500);
        for (int i = 1; i <= 500; i++) {
            long customerId = i / 10;
            double amount = i;
            new Thread(() -> {
                try {
                    String session = sessionService.getSession(customerId);
                    stakeService.offerStake(bitOfferId, session, amount);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cdl.countDown();
                }
            }).start();
        }
        cdl.await();
        System.out.println("*********************** wait all thead finish ************************************");
        String s = stakeService.highStakes(bitOfferId);
        System.out.println(s);
    }

}
