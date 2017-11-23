package com.tfl.billing;


import com.tfl.external.Customer;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;



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
    public void chargeNothingIfNoJourneysMade() throws Exception {
        ControllableCustomerDatabase database = new ControllableCustomerDatabase();
        database.add(new Customer("John Smith",new OysterCardID("38400000-8cf0-11bd-b23e-10b96e4ef00d").getCard()));
        TestBillingSystem system = new TestBillingSystem();
        TravelTracker tracker = Mockito.spy(new TravelTracker(system,database));

        tracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }
    @Test
    public void chargeIfJourneyIsMade() throws Exception {
        ControllableCustomerDatabase database = new ControllableCustomerDatabase();
        OysterCardID myCard = new OysterCardID("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        database.add(new Customer("John Smith",myCard.getCard()));

        OysterCardIDReader paddingtonReader = new OysterCardIDReader(Station.PADDINGTON);
        OysterCardIDReader barbicanReader = new OysterCardIDReader(Station.BARBICAN);
        TestBillingSystem system = new TestBillingSystem();
        ControllableClock clock = new ControllableClock();
        TravelTracker tracker = new TravelTracker(system,database,clock);
        tracker.connect(paddingtonReader,barbicanReader);

        clock.setTIme(10,00);
        paddingtonReader.touch(myCard);
        clock.setTIme(10,15);
        barbicanReader.touch(myCard);
        tracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(2.40).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void chargePeakIfPeakJourneyIsMade() throws Exception {
        ControllableCustomerDatabase database = new ControllableCustomerDatabase();
        OysterCardID myCard = new OysterCardID("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        database.add(new Customer("John Smith",myCard.getCard()));

        OysterCardIDReader paddingtonReader = new OysterCardIDReader(Station.PADDINGTON);
        OysterCardIDReader barbicanReader = new OysterCardIDReader(Station.BARBICAN);
        TestBillingSystem system = new TestBillingSystem();
        ControllableClock clock = new ControllableClock();
        TravelTracker tracker = new TravelTracker(system,database,clock);
        tracker.connect(paddingtonReader,barbicanReader);

        clock.setTIme(9,00);
        paddingtonReader.touch(myCard);
        clock.setTIme(9,15);
        barbicanReader.touch(myCard);
        tracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(3.20).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void chargeLongIfLongJourneyIsMade() throws Exception {
        ControllableCustomerDatabase database = new ControllableCustomerDatabase();
        OysterCardID myCard = new OysterCardID("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        database.add(new Customer("John Smith",myCard.getCard()));

        OysterCardIDReader paddingtonReader = new OysterCardIDReader(Station.PADDINGTON);
        OysterCardIDReader barbicanReader = new OysterCardIDReader(Station.BARBICAN);
        TestBillingSystem system = new TestBillingSystem();
        ControllableClock clock = new ControllableClock();
        TravelTracker tracker = new TravelTracker(system,database,clock);
        tracker.connect(paddingtonReader,barbicanReader);

        clock.setTIme(10,00);
        paddingtonReader.touch(myCard);
        clock.setTIme(10,30);
        barbicanReader.touch(myCard);
        tracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(2.70).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }
    @Test
    public void chargeLongPeakIfLongPeakJourneyIsMade() throws Exception {
        ControllableCustomerDatabase database = new ControllableCustomerDatabase();
        OysterCardID myCard = new OysterCardID("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        database.add(new Customer("John Smith",myCard.getCard()));

        OysterCardIDReader paddingtonReader = new OysterCardIDReader(Station.PADDINGTON);
        OysterCardIDReader barbicanReader = new OysterCardIDReader(Station.BARBICAN);
        TestBillingSystem system = new TestBillingSystem();
        ControllableClock clock = new ControllableClock();
        TravelTracker tracker = new TravelTracker(system,database,clock);
        tracker.connect(paddingtonReader,barbicanReader);

        clock.setTIme(9,00);
        paddingtonReader.touch(myCard);
        clock.setTIme(9,30);
        barbicanReader.touch(myCard);
        tracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(3.80).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }



    private class ControllableClock implements Clock{

        LocalTime time = LocalTime.now();

        @Override
        public long getNow() {
            return (long)(time.toNanoOfDay()/1e6);
        }

        public void setTIme(int hour,int minute){
            time = LocalTime.of(hour-1,minute,0,0);
        }
    }



}