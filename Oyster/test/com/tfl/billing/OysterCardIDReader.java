package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OysterCardIDReader implements IdentificationReader{

    OysterCardReader reader;

    public OysterCardIDReader(Station station){
        this.reader = OysterReaderLocator.atStation(station);
    }

    @Override
    public void register(ScanListener eventListener) {
        reader.register(eventListener);
    }

    @Override
    public UUID id() {
        return reader.id();
    }


    @Override
    public void touch(Identification card) {
        OysterCard oCard = new OysterCard(card.id().toString());
        reader.touch(oCard);
    }

}
