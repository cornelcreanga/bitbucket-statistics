package com.ccreanga.bitbucketapi.example;

import java.util.Calendar;
import java.util.Date;

public class Interval {

    private final Date startDate;
    private final Date endDate;

    public Interval(Date startDate, Date endDate) {
        Utils.checkDates(startDate,endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public static Interval daysAgo(int days){
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR,-days);
        Date startDate = calendar.getTime();
        return new Interval(startDate,endDate);
    }
}
