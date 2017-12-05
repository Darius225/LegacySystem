package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TravelTracker extends Tracker
{


    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();

     protected boolean isTraveling(UUID id){
        if (currentlyTravelling.contains(id)) {
            return  true;
        } else {
            return false;
        }
    }

    public TravelTracker(){

        this(new BillingSystem() {
            @Override
            public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
                PaymentsSystem.getInstance().charge(customer, journeys, totalBill);
            }
        }, new MainCustomerDatabase(),new Cache());
        calculator = new StandardCostCalculator();
    }

    public TravelTracker(BillingSystem system,EntityDatabase database , Cache cache){
        this.billingSystem = system;
        this.entityDatabase = database;
        this.cache = cache ;
        calculator = new StandardCostCalculator();
    }



    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            JourneyEvent event = new JourneyEnd(cardId, readerId) ;
            cache.add_Journey( event ) ;
            currentlyTravelling.remove(cardId);
        } else {
            if (entityDatabase.isRegisteredId(cardId)) {
                JourneyEvent event = new JourneyStart(cardId, readerId) ;
                currentlyTravelling.add(cardId);
                cache.add_Journey( event ) ;
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }


}
