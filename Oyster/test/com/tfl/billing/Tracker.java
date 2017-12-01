package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.*;

public abstract class Tracker implements ScanListener{
    static final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    static final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);

    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();


    protected BillingSystem billingSystem;
    protected EntityDatabase entityDatabase;

    Cache cache ;
    protected boolean isTraveling(UUID id){
        if (currentlyTravelling.contains(id)) {
            return  true;
        } else {
            return false;
        }
    }

    public void chargeAccounts() {
        List<Customer> customers = entityDatabase.getCustomers();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }

    private void totalJourneysFor(Customer customer) { // Proxy pattern


        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();
        customerJourneyEvents = cache.get_Journeys_By_Customer( customer.cardId() ) ;
        System.out.println(customerJourneyEvents.size());
        List<Journey> journeys = convertJourneyEvents(customerJourneyEvents);
        BigDecimal customerTotal = new BigDecimal(0);
        customerTotal = calculateJourneyCost(journeys, customerTotal);
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

    private BigDecimal calculateJourneyCost(List<Journey> journeys, BigDecimal customerTotal) {
        boolean isOneJourneyPeak = false;
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = OFF_PEAK_JOURNEY_PRICE;
            if (peak(journey)) {
                journeyPrice = PEAK_JOURNEY_PRICE;
                isOneJourneyPeak = true;
            }
            if(isLong(journey)){
                if (peak(journey)) {
                    journeyPrice = PEAK_LONG_JOURNEY_PRICE;
                    isOneJourneyPeak = true;
                }else{
                    journeyPrice = OFF_PEAK_LONG_JOURNEY_PRICE;
                }
            }

            customerTotal = customerTotal.add(journeyPrice);
            if(isOneJourneyPeak) {
                if (customerTotal.floatValue() > 9) {
                    customerTotal = new BigDecimal(9);
                }
            }else {
                if (customerTotal.floatValue() > 7) {
                    customerTotal = new BigDecimal(7);
                }
            }
        }
        return customerTotal;
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isLong(Journey journey){
        int minutes = Integer.parseInt(journey.durationMinutes().split(":")[0]);
        if(minutes<25){
            return false;
        }return  true;

    }

    private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
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
