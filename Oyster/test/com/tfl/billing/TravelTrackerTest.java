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
import java.util.ArrayList;

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
    public void chargeNothingIfNoJourneysMade() throws Exception
    {
        charge_Correctly( 0 ,new int [] {} , new int [] {} , 0 );
    }
    @Test
    public void chargeIfJourneyIsMade() throws Exception
    {
        single_journey ( 10 , 0 , 15 , 2.40 ) ;
    }
    @Test
    public void chargePeakIfPeakJourneyIsMade() throws Exception
    {
        single_journey (9,0,15,3.20 ) ;
    }
    @Test
    public void chargeLongIfLongJourneyIsMade() throws Exception
    {
        single_journey ( 10 ,0,30,2.70 );
    }
    @Test
    public void chargeLongPeakIfLongPeakJourneyIsMade() throws Exception {
        single_journey ( 9 , 0 , 30 , 3.80 ) ;
    }
    @Test
    public void capOffPeak()
    {
        charge_Correctly( 6 , new int [ ] { 10 , 10 , 10, 11 , 11 , 11 } , new int [ ] { 00 , 15 , 55 , 15 , 20 , 55 } , 7.00 );
    }
    @Test
    public void capPeak()
    {
        charge_Correctly( 6 , new int [ ] { 9 , 9 , 9 , 10 , 10 , 10 } , new int [ ] { 00 , 15 , 55 , 15 , 20 , 55 } , 9.00 );
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
    public void single_journey ( int hour , int minute , int length , double total )
    {
           int newMinute = minute + length ;
           charge_Correctly ( 2 , new int[] { hour , hour + newMinute / 60 } , new int[] {minute , newMinute % 60 } , total );
    }
    public void charge_Correctly  ( int no_touches , int hour [ ] , int minutes [ ] , double total )
    {
        ControllableCustomerDatabase database = new ControllableCustomerDatabase();
        OysterCardID myCard = new OysterCardID("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        database.add(new Customer("John Smith",myCard.getCard()));

        OysterCardIDReader paddingtonReader = new OysterCardIDReader("38400000-8cf0-11bd-b23e-10b96e4ef10d");
        OysterCardIDReader barbicanReader = new OysterCardIDReader("38402800-8cf0-11bd-b23e-10b96e4ef00d");
        TestBillingSystem system = new TestBillingSystem();
        ControllableClock clock = new ControllableClock();
        Cache c = new Cache () ;
        JourneyBuilder builder = new JourneyBuilder(clock,database,c);
        ArrayList <OysterCardIDReader > readers = new ArrayList< >();
        readers.add ( paddingtonReader ) ;
        readers.add ( barbicanReader ) ;
        for ( int i = 0 ; i < no_touches ; i ++ )
        {
            clock.setTIme ( hour [ i ] , minutes  [ i ] ) ;
            builder.addEvent ( myCard , readers.get ( i % 2 ) ) ;
        }
        TravelTracker tracker = new TravelTracker(system,database,builder.getEventLog(),c);

        tracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

}
