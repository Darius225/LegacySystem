package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.*;

public abstract class Tracker implements ScanListener{



    protected BillingSystem billingSystem;
    protected EntityDatabase entityDatabase;

    protected CostCalculator calculator;

    protected Cache cache ;




    public void chargeAccounts() {
        List<Customer> customers = entityDatabase.getCustomers();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }



    private void totalJourneysFor(Customer customer) { // Proxy pattern

        List<JourneyEvent> customerJourneyEvents;
        customerJourneyEvents = cache.get_Journeys_By_Customer( customer.cardId() ) ;
        System.out.println(customerJourneyEvents.size());
        List<Journey> journeys = convertJourneyEvents(customerJourneyEvents);
        BigDecimal customerTotal = new BigDecimal(0);
        customerTotal = calculator.calculateJourneyCost(journeys, customerTotal);
        billingSystem.charge(customer,journeys,roundToNearestPenny(customerTotal));
        //PaymentsSystem.getInstance().charge(customer, journeys, roundToNearestPenny(customerTotal));
    }

    private List<Journey> convertJourneyEvents(List<JourneyEvent> customerJourneyEvents) {
        List<Journey> journeys = new ArrayList<Journey>();
        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }
        return journeys;
    }


    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }
    public void connect(IdentificationReader... cardReaders) {
        for (IdentificationReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }
    @Override
    public abstract void cardScanned(UUID cardId, UUID readerId);

}
