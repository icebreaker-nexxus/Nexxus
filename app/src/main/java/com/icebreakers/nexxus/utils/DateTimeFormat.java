package com.icebreakers.nexxus.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amodi on 4/16/17.
 */

public class DateTimeFormat {

    public static String formatDateTimeForEvent(long timestamp) {
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("EEE, MMM d, hh:mm aaa");
        return formatter.format(date);
    }
}
