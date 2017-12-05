package com.tfl.billing;


import java.util.UUID;

public class ProxyTracker extends Tracker {
    private Clock clock ;
    public ProxyTracker(MockBillingSystem system, ControllableCustomerDatabase database, Cache cache  ) {
           this.billingSystem = system ;
           this.entityDatabase = database ;
           this.cache = cache ;
           calculator = new StandardCostCalculator();
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId)
    {

    }
    public void connect(IdentificationReader... cardReaders) {
        for (IdentificationReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }
}
