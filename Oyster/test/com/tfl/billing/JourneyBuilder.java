package com.tfl.billing;

import java.util.*;

public class JourneyBuilder {

    private final List<JourneyEvent> eventLog = new ArrayList<>();
    private final Set<UUID> currentlyTravelling = new HashSet<UUID>();

    EntityDatabase database;
    Clock clock;
    public JourneyBuilder(Clock clock, EntityDatabase entityDatabase){
        this.database = entityDatabase;
        this.clock = clock;
    }

    public List<JourneyEvent> getEventLog(){
        return eventLog;
    }

    public void addEvent(Identification cardId, IdentificationReader readerId) {
        if (currentlyTravelling.contains(cardId.id())) {
            eventLog.add(new JourneyEnd(cardId.id(), readerId.id(),clock));
            currentlyTravelling.remove(cardId.id());
        } else {
            if (database.isRegisteredId(cardId.id())) {
                currentlyTravelling.add(cardId.id());
                eventLog.add(new JourneyStart(cardId.id(), readerId.id(),clock));
            } else {
                throw new UnknownOysterCardException(cardId.id());
            }
        }
    }

}
