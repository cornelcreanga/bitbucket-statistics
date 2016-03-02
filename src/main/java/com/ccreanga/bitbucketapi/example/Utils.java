package com.ccreanga.bitbucketapi.example;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.base.Preconditions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public static byte[] serialize(Object object){
        Kryo kryo = new Kryo();
        Output output = new Output(4096);
        kryo.writeClassAndObject(output,object);
        output.close();
        return output.toBytes();
    }

    public static Object deserialize(byte[] data){
        Kryo kryo = new Kryo();
        return kryo.readClassAndObject(new Input(data));

    }


}
