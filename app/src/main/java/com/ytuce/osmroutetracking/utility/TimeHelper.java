package com.ytuce.osmroutetracking.utility;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {

    public static String timestampToDate(long timestamp) {
        DateFormat format = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT, new Locale("tr", "TR"));
        Date date = new Date(timestamp);
        return format.format(date);
    }

}
