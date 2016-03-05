package com.ccreanga.bitbucketapi.example;

import com.ccreanga.bitbucket.rest.client.model.User;
import com.ccreanga.bitbucket.rest.client.model.pull.PullRequest;
import com.google.common.base.Preconditions;

import java.util.*;

public class Utils {

    public static Date truncate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);

        return calendar.getTime();
    }

    public static void checkDates(Date startDate, Date endDate){
        Preconditions.checkNotNull(startDate,"startDate is null");
        Preconditions.checkNotNull(endDate,"endDate is null");
        Preconditions.checkArgument(endDate.compareTo(startDate)>=0,"end date should be greater than start date");
    }

    private static void mergeMaps(Map<User, List<Object>> firstMap, Map<User, List<Object>> secondMap){
        Set<User> users = secondMap.keySet();
        for (User next : users) {
            List<Object> values = firstMap.get(next);
            if (values == null)
                firstMap.put(next, secondMap.get(next));
            else {
                values.addAll(secondMap.get(next));
                firstMap.put(next, values);
            }
        }
    }


}
