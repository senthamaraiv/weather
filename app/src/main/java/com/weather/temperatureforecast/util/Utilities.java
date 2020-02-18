package com.weather.temperatureforecast.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

    public static String getHumanReadable(long convert_me){

        // the format of the required date
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");

        // set the timezone reference for formatting
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC+1"));

        // get the date in milliseconds
        Date date_value = new java.util.Date(convert_me * 1000L);

        return sdf.format(date_value);
    }

}
