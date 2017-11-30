package com.tfl.billing;


import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;



public class TravelTrackerTest 
{

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Rule public JUnitRuleMockery context = new JUnitRuleMockery();
    @Test
    public void connect() throws Exception 
    {
        TravelTracker tracker = Mockito.spy(new TravelTracker());
        OysterCardReader reader = Mockito.spy(OysterReaderLocator.atStation(Station.PADDINGTON));
        tracker.connect(reader,reader2);
        Mockito.verify(reader).register(tracker);
    }
    @Test
    public void cardScannedOnce() throws Exception
    {
        TravelTracker tracker = Mockito.spy(new TravelTracker());
        OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        IdentificationReader reader = new OysterCardIDReader(Station.VICTORIA_STATION);
        tracker.connect(reader);
        reader.touch(myCard);
        Mockito.verify(tracker).cardScanned(myCard.id(),reader.id());
        assertThat(tracker.isTraveling(myCard.id()),is(true));
    }
    @Test
    public void cardScannedTwice() throws Exception
    {
        TravelTracker tracker = Mockito.spy(new TravelTracker());
        OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        IdentificationReader reader = new OysterCardIDReader(Station.VICTORIA_STATION);
        tracker.connect(reader);
        reader.touch(myCard);
        reader.touch(myCard);
        Mockito.verify(tracker,Mockito.times(2)).cardScanned(myCard.id(),reader.id());
        assertThat(tracker.isTraveling(myCard.id()),is(false));
    }
    @Test
    public void unknownCardScanned() throws Exception
    {
        TravelTracker tracker = Mockito.spy(new TravelTracker());
        OysterCard myCard = new OysterCard("38400332-8cf0-11bd-b23e-10b96e4ef00d");
        IdentificationReader reader = new OysterCardIDReader(Station.VICTORIA_STATION);
        tracker.connect(reader);

        exception.expect(UnknownOysterCardException.class);

        reader.touch(myCard);

    }

    @Test
    public void chargeNothingIfNoJourneysMade() throws Exception
    {
        no_journey ( ) ;
    }
    @Test
    public void chargeIfJourneyIsMade() throws Exception
    {
        single_journey ( 10 , 0 , 15 , 2.40 ) ;
    }
    @Test
    public void chargePeakIfPeakJourneyIsMade() throws Exception
    {
        single_journey ( 9 ,  0 , 15 , 3.20 ) ;
    }
    @Test
    public void chargeLongIfLongJourneyIsMade() throws Exception
    {
        single_journey ( 10 , 0 , 30 , 2.70 );
    }
    @Test
    public void chargeLongPeakIfLongPeakJourneyIsMade() throws Exception 
    {
        single_journey ( 9  , 0 , 30 , 3.80 ) ;
    }
    @Test
    public void capOffPeak()
    {
        charge_Sequence_Of_Events ( 6 , new int [ ] { 10 , 10 , 10 , 11 , 11 , 11 } , new int [ ] { 00 , 15 , 55 , 15 , 20 , 55 } , 7.00 );
    }
    @Test
    public void capPeak()
    {
        charge_Sequence_Of_Events ( 6 , new int [ ] {  9 ,  9 ,  9 , 10 , 10 , 10 } , new int [ ] { 00 , 15 , 55 , 15 , 20 , 55 } , 9.00 );
    }

    private class ControllableClock implements Clock 
    {

        LocalTime time = LocalTime.now();

        @Override
        public long getNow() 
        {
            return (long)(time.toNanoOfDay()/1e6);
        }

        public void setTIme(int hour,int minute)
        {
            time = LocalTime.of(hour-1,minute,0,0);
        }
    }
    public void no_journey( ) // If no journey is made,then there are 0 events .
    {
           charge_Sequence_Of_Events( 0 , new int [ ] {} , new int [ ] {} , 0 );
    }
    public void single_journey ( int start_Hour , int start_Minute , int length , double expected_Total ) // A journey can be seen as a sequence of 2 events .
    {
           int end_Minute = start_Minute + length ; // Determine the end minute and hour of the journey.We should take into account the case when the end hour is different from the start hour .
           int end_Hour = start_Hour + end_Minute / 60 ;
           end_Minute = end_Minute % 60 ;
           charge_Sequence_Of_Events ( 2 , new int[] { start_Hour , end_Hour } , new int[] { start_Minute , end_Minute  } , expected_Total ) ;
    }
    public void charge_Sequence_Of_Events ( int no_events , int hour [ ] , int minute [ ] , double expected_Total )
    {
        ControllableCustomerDatabase database = new ControllableCustomerDatabase();
        OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        database.add(new Customer("John Smith",myCard));

        OysterCardIDReader paddingtonReader = new OysterCardIDReader("38400000-8cf0-11bd-b23e-10b96e4ef10d");
        OysterCardIDReader barbicanReader = new OysterCardIDReader("38402800-8cf0-11bd-b23e-10b96e4ef00d");
        MockBillingSystem system = new MockBillingSystem();
        ControllableClock clock = new ControllableClock();
        Cache c = new Cache () ;
        JourneyBuilder builder = new JourneyBuilder(clock,database);
        ArrayList <OysterCardIDReader > readers = new ArrayList< >();
        readers.add ( paddingtonReader ) ;
        readers.add ( barbicanReader ) ;
        for ( int i = 0 ; i < no_events ; i ++ )
        {
            clock.setTIme ( hour [ i ] , minute  [ i ] ) ;
            builder.addEvent ( myCard , readers.get ( i % 2 ) ) ;
        }
        TravelTracker tracker = new TravelTracker(system,database,builder.getEventLog(),c);

        tracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(expected_Total).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

}
