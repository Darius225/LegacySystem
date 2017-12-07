package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.identification.IdentificationReader;

import java.util.*;

public class JourneyBuilder {

    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();

    EntityDatabase database;
    Clock clock ;
    Cache cache ;

    public JourneyBuilder(Clock clock, EntityDatabase entityDatabase, Cache cache){
        this.database = entityDatabase;
        this.clock = clock;
        this.cache = cache ;
    }


    public void addEvent(OysterCard cardId, IdentificationReader readerId) {
        if (currentlyTravelling.contains(cardId.id())) {
            JourneyEvent event = new JourneyEnd(cardId.id(), readerId.id(),clock) ;
            cache.add_Journey( event );
            currentlyTravelling.remove(cardId.id());
        } else {
            if (database.isRegisteredId(cardId.id())) {
                JourneyEvent event = new JourneyStart(cardId.id(), readerId.id(),clock) ;
                currentlyTravelling.add(cardId.id());
                cache.add_Journey( event );
            } else {
                throw new UnknownOysterCardException(cardId.id());
            }
        }
    }

}
