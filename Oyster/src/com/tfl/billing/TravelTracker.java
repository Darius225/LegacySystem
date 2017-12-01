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

    static final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    static final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);


    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();


    protected BillingSystem billingSystem;
    protected EntityDatabase entityDatabase;

    private Clock clock;
    private Cache cache = new Cache();


    public TravelTracker(){
        this(new BillingSystem() {
            @Override
            public void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill) {
                PaymentsSystem.getInstance().charge(customer, journeys, totalBill);
            }
        }, new MainCustomerDatabase());
    }
    public TravelTracker(BillingSystem system){
        this(system,new MainCustomerDatabase() );
    }
    public TravelTracker(BillingSystem system,EntityDatabase database){
        this(system,database,new Clock(){
            public long getNow(){
                return System.currentTimeMillis();
            }
        });
    }
    public TravelTracker(BillingSystem system,EntityDatabase database,Clock clock){
        this.billingSystem = system;
        this.entityDatabase = database;
        this.clock = clock;
    }
    public TravelTracker(BillingSystem system,EntityDatabase database , Cache cache){
        this.billingSystem = system;
        this.entityDatabase = database;
        this.cache = cache ;
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
