package us.spencer.habittracker.utility;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;

public class DateUtils {

    private static final String[] DAY_OF_MONTH = {
            "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22",
            "23", "24", "25", "26", "27", "28", "29",
            "30", "31"
    };

    private static final String[] MONTH_OF_YEAR = {
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG",
            "SEP", "OCT", "NOV", "DEC"
    };

    private static String[] DAY_OF_WEEK = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"};

    public static String getDayOfMonthAsString(int dayOfMonth) {
        return DAY_OF_MONTH[dayOfMonth - 1];
    }

    public static String getMonthAsString(int month) {
        return  MONTH_OF_YEAR[month - 1];
    }

    public static String getDayOfWeek(int dayOfWeek) {
        return DAY_OF_WEEK[dayOfWeek - 1];
    }

    public static MutableDateTime getCurrentDate() {
        MutableDateTime instant = new MutableDateTime(Instant.now());
        instant.setZone(DateTimeZone.getDefault());
        instant.setHourOfDay(0);
        instant.setMinuteOfDay(0);
        instant.setSecondOfDay(0);
        instant.setMillisOfDay(0);
        return instant;
    }
}
