package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.junit.MockitoRule;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.junit.MockitoJUnit.rule;
public class TravelTrackerTest {
    @Rule public MockitoRule context = rule();
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    TravelTracker travelTracker = Mockito.mock(TravelTracker.class);
    TravelTracker realTracker = new TravelTracker() ;
    @Test
    public void chargeAccounts()
    {
        travelTracker.chargeAccounts() ;
        verify(travelTracker, Mockito.times(1)).chargeAccounts();
    }
    @Test
    public void  connect ()
    {
        OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00e");
        OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);
        realTracker.connect ( paddingtonReader, bakerStreetReader, kingsCrossReader);
        verify(travelTracker, Mockito.times(1) ).connect( paddingtonReader , bakerStreetReader , kingsCrossReader ); ;
    }
    @Test
    public void cardScanned()
    {
        Throwable exception = assertThrows(UnknownOysterCardException.class, () -> {
             realTracker.cardScanned (  UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00e") , UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d" ) );
        });
    }
}