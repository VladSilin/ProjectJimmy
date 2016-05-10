package com.pig.jimmy.util;

/**
 * Created by Vlad Silin on 25/03/16.
 *
 * A utility class for dealing with dates and times
 */
public class DateTimeUtils {
    private DateTimeUtils() {
    }

    public static int getSeconds(int hours, int minutes) {
        return hours * 3600 + minutes * 60;
    }
}
