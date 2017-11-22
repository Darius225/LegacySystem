import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.TravelTracker;
import com.tfl.billing.UnknownOysterCardException;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.UUID;

public class Example {
    public static void main(String[] args) throws Exception {
        OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        TravelTracker travelTracker = new TravelTracker();
        System.out.println (  new UnknownOysterCardException( UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00e") ) .getMessage() ) ;
    }

    private static void minutesPass(int n) throws InterruptedException {
        Thread.sleep(n * 60 * 1000);
    }
}