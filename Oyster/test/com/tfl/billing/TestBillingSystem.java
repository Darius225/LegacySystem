package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;

public class TestBillingSystem implements BillingSystem {


    private BigDecimal totalBill;



    @Override
    public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
            this.totalBill = totalBill;
        PaymentsSystem.getInstance().charge(customer,journeys,totalBill);
    }

    public BigDecimal getTotalBill() {
        return totalBill;
    }
}
