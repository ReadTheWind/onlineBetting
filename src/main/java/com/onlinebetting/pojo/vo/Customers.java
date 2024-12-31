package com.onlinebetting.pojo.vo;

import java.util.concurrent.ConcurrentSkipListSet;


public class Customers {

    private ConcurrentSkipListSet<Long> customerIds;

    public Customers() {
        this.customerIds = new ConcurrentSkipListSet<>();
    }

    public Customers addCustomerId(Long customerId) {
        customerIds.add(customerId);
        return this;
    }

    public ConcurrentSkipListSet<Long> getCustomerIds() {
        return customerIds;
    }

    public void clear() {
        customerIds.clear();
    }

}
