package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.UUID;

public class OysterCardIDReader implements IdentificationReader{

    OysterCardReader reader;
    UUID uuid;

    public OysterCardIDReader(Station station){
        this.reader = OysterReaderLocator.atStation(station);
        uuid = reader.id();
    }
    public OysterCardIDReader(String id){
        uuid = UUID.fromString(id);
    }

    @Override
    public void register(ScanListener eventListener) {
        reader.register(eventListener);
    }

    @Override
    public UUID id() {
        return uuid;
    }


    @Override
    public void touch(OysterCard card) {
        reader.touch(card);
    }

}
