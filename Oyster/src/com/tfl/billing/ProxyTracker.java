package com.tfl.billing;


import java.util.UUID;

public class ProxyTracker extends Tracker {
    private Clock clock ;
    public ProxyTracker(MockBillingSystem system, ControllableCustomerDatabase database, Cache cache  ) {
           this.billingSystem = system ;
           this.entityDatabase = database ;
           this.cache = cache ;
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId)
    {

    }
}
