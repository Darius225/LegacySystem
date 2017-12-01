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
    private final OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private TravelTracker mockTracker = Mockito.spy(new TravelTracker());
    private final IdentificationReader victoriaReader = new OysterCardIDReader(Station.VICTORIA_STATION);
    private boolean[] travelling = new boolean [ 2 ] ;
    private final OysterCardIDReader paddingtonReader = new OysterCardIDReader("38400000-8cf0-11bd-b23e-10b96e4ef10d");
    private final OysterCardIDReader barbicanReader = new OysterCardIDReader("38402800-8cf0-11bd-b23e-10b96e4ef00d");
    private final OysterCard nonExistentCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00e") ;
    private final OysterCardReader paddingtonMockReader = Mockito.spy(OysterReaderLocator.atStation(Station.PADDINGTON));
    private ControllableCustomerDatabase database = new ControllableCustomerDatabase();
    private MockBillingSystem system = new MockBillingSystem();
    private ControllableClock clock = new ControllableClock();
    private Cache c = new Cache () ;
    private ArrayList <OysterCardIDReader> readers = new ArrayList<>() ;
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    @Test
    public void connect() throws Exception
    {

        mockTracker.connect(paddingtonMockReader);
        Mockito.verify(paddingtonMockReader).register(mockTracker);
    }
    @Test
    public void cardScannedOnce() throws Exception
    {
        cardScannedSomeNumberOfTimes( 1 );
    }
    @Test
    public void cardScannedTwice() throws Exception
    {
        cardScannedSomeNumberOfTimes( 2 );
    }
    @Test
    public void unknownCardScanned() throws Exception
    {
        mockTracker.connect(victoriaReader);
        exception.expect(UnknownOysterCardException.class);
        victoriaReader.touch(nonExistentCard);
    }

    @Test
    public void chargeNothingIfNoJourneysMade() throws Exception
    {
        noJourney ( ) ;
    }
    @Test
    public void chargeIfJourneyIsMade() throws Exception
    {
        singleJourney ( 10 , 0 , 15 , 2.40 ) ;
    }
    @Test
    public void chargePeakIfPeakJourneyIsMade() throws Exception
    {
        singleJourney ( 9 ,  0 , 15 , 3.20 ) ;
    }
    @Test
    public void chargeLongIfLongJourneyIsMade() throws Exception
    {
        singleJourney ( 10 , 0 , 30 , 2.70 );
    }
    @Test
    public void chargeLongPeakIfLongPeakJourneyIsMade() throws Exception
    {
        singleJourney ( 9  , 0 , 30 , 3.80 ) ;
    }
    @Test
    public void capOffPeak()
    {
        chargeSequenceOfEvents ( 6 , new int [ ] { 10 , 10 , 10 , 11 , 11 , 11 } , new int [ ] { 00 , 15 , 55 , 15 , 20 , 55 } , 7.00 );
    }
    @Test
    public void capPeak()
    {
        chargeSequenceOfEvents ( 6 , new int [ ] {  9 ,  9 ,  9 , 10 , 10 , 10 } , new int [ ] { 00 , 15 , 55 , 15 , 20 , 55 } , 9.00 );
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
    public void noJourney( ) // If no journey is made,then there are 0 events .
    {
        chargeSequenceOfEvents( 0 , new int [ ] {} , new int [ ] {} , 0 );
    }
    public void singleJourney ( int startHour , int startMinute , int length , double expectedTotal ) // A journey can be seen as a sequence of 2 events .
    {
        int endMinute = startMinute + length ; // Determine the end minute and hour of the journey.We should take into account the case when the end hour is different from the start hour .
        int endHour = startHour + endMinute / 60 ;
        endMinute = endMinute % 60 ;
        chargeSequenceOfEvents ( 2 , new int[] { startHour , endHour } , new int[] { startMinute , endMinute  } , expectedTotal ) ;
    }
    public void chargeSequenceOfEvents ( int noEvents , int hour [ ] , int minute [ ] , double expectedTotal )
    {
        database.add(new Customer("John Smith",myCard));
        JourneyBuilder builder = new JourneyBuilder(clock,database,c );

        readers.add ( paddingtonReader ) ;
        readers.add ( barbicanReader ) ;
        for ( int i = 0 ; i < noEvents ; i ++ )
        {
            clock.setTIme ( hour [ i ] , minute  [ i ] ) ;
            builder.addEvent ( myCard , readers.get ( i % 2 ) ) ;
        }
        ProxyTracker fakeTracker = new ProxyTracker(system,database,c);

        fakeTracker.chargeAccounts();
        assertThat(system.getTotalBill(),is(new BigDecimal(expectedTotal).setScale(2, BigDecimal.ROUND_HALF_UP)));
    }
    private void cardScannedSomeNumberOfTimes ( int noTimes ) throws Exception
    {
        travelling [ 0 ] = false ;
        travelling [ 1 ] = true ;
        mockTracker.connect ( victoriaReader );
        for ( int i = 1 ; i <= noTimes ; i ++ )
        {
            victoriaReader.touch(myCard);
        }
        Mockito.verify(mockTracker,Mockito.times(noTimes)).cardScanned(myCard.id(),victoriaReader.id());
        assertThat(mockTracker.isTraveling(myCard.id()),is( travelling [ noTimes % 2 ] ));
    }
}
