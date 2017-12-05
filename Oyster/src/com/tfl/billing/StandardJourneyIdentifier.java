package com.tfl.billing;

import java.util.Calendar;
import java.util.Date;

public class StandardJourneyIdentifier implements JourneyIdentifier {




    @Override
    public boolean isLong(Journey journey){
        int minutes = Integer.parseInt(journey.durationMinutes().split(":")[0]);
        if(minutes<25){
            return false;
        }return  true;

    }

    @Override
    public boolean isPeak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }
}
