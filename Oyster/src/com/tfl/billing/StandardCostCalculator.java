package com.tfl.billing;

import java.math.BigDecimal;
import java.util.List;

public class StandardCostCalculator implements CostCalculator {

    private JourneyIdentifier identifier = new StandardJourneyIdentifier();

    @Override
    public BigDecimal calculateJourneyCost(List<Journey> journeys, BigDecimal customerTotal) {
        boolean isOneJourneyPeak = false;
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = Cost.OFF_PEAK_JOURNEY_PRICE;
            if (identifier.isPeak(journey)) {
                journeyPrice = Cost.PEAK_JOURNEY_PRICE;
                isOneJourneyPeak = true;
            }
            if(identifier.isLong(journey)){
                if (identifier.isPeak(journey)) {
                    journeyPrice = Cost.PEAK_LONG_JOURNEY_PRICE;
                    isOneJourneyPeak = true;
                }else{
                    journeyPrice = Cost.OFF_PEAK_LONG_JOURNEY_PRICE;
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


}
