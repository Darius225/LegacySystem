package com.tfl.billing;

import com.oyster.OysterCard;

import java.util.UUID;

public class OysterCardID implements Identification {

    OysterCard card;


    public OysterCardID(String id){
        this.card = new OysterCard(id);
    }

    @Override
    public UUID id() {
        return  card.id();
    }
}
