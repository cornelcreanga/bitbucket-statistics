package com.ccreanga.bitbucketapi.example;

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
}
