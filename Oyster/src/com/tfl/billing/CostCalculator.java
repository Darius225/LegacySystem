package com.tfl.billing;

import java.math.BigDecimal;
import java.util.List;

public interface CostCalculator {
    BigDecimal calculateJourneyCost(List<Journey> journeys, BigDecimal customerTotal);
}
