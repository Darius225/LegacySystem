package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;
import static org.mockito.junit.MockitoJUnit.rule;
public class TravelTrackerTest {
    @Rule public MockitoRule context = rule();

    TravelTracker travelTracker = Mockito.mock(TravelTracker.class); ;
    TravelTracker test = new TravelTracker() ;
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
        travelTracker.connect ( paddingtonReader, bakerStreetReader, kingsCrossReader);
        verify(travelTracker, Mockito.times(1) ).connect( paddingtonReader , bakerStreetReader , kingsCrossReader ); ;
    }
    @Test
    public void cardScanned ()
    {
        OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d") ;

    }
}