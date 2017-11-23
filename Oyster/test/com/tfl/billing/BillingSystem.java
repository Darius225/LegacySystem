package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface BillingSystem {

    void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill);
}
