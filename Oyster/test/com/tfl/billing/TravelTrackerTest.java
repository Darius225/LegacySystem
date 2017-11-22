package com.tfl.billing;


import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;



public class TravelTrackerTest {



    @Rule public JUnitRuleMockery context = new JUnitRuleMockery();

    @Test
    public void connect() throws Exception {
        TravelTracker tracker = new TravelTracker();
        IdentificationReader reader = context.mock(IdentificationReader.class,"reader1");
        IdentificationReader reader2 = context.mock(IdentificationReader.class,"reader2");
        context.checking(new Expectations() {{
            exactly(1).of(reader).register(tracker);
            exactly(1).of(reader2).register(tracker);
        }});
        tracker.connect(reader,reader2);
    }

    @Test
    public void cardScanned() throws Exception {
        TravelTracker tracker = Mockito.spy(new TravelTracker());
        Identification myCard = new OysterCardID("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        IdentificationReader reader = new OysterCardIDReader(Station.VICTORIA_STATION);
        tracker.connect(reader);
        reader.touch(myCard);
        Mockito.verify(tracker).cardScanned(myCard.id(),reader.id());
    }
    @Test
    public void chargeAccounts() throws Exception {
        TravelTracker tracker = Mockito.spy(new TravelTracker());
        tracker.chargeAccounts();

        Mockito.verify(tracker).chargeAccounts();
    }



}