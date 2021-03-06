package us.spencer.habittracker.model;

import android.support.annotation.NonNull;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.ISOChronology;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents an instant in time in which a particular event occurred.
 * It is focused on the date of the event for now. It will fix the time
 * to a specified amount. In this case three in the afternoon. It also ensures it
 * uses the same {@link Chronology} across all creations of {@link TimeStamp} objects.
 */
public class TimeStamp {

    private static final Chronology ISO_CHRONOLOGY = ISOChronology.getInstance();

    private static final int FIXED_HOUR = 15;

    private static final int FIXED_MINUTES = 0;

    private static final int FIXED_SECONDS = 0;

    private static final int FIXED_MILLI = 0;

    /**
     * The instant in which the event occurred.
     * Should account for local time settings on
     * device.
     */
    private final DateTime mDateTime;

    /**
     * Constructs a {@link TimeStamp} containing the
     * {@link LocalDateTime} that represents the time the
     * event occurred.
     *
     * @param instant   the time the event occurred
     */
    public TimeStamp(@NonNull Instant instant) {
        mDateTime = new DateTime(instant, ISO_CHRONOLOGY)
                        .withTime(FIXED_HOUR, FIXED_MINUTES, FIXED_SECONDS, FIXED_MILLI);
    }

    /**
     * Constructs a {@link TimeStamp} containing the {@link Instant}
     * at the specified milliseconds. It stores it as a {@link LocalDateTime}
     * to use in calendar interface later.
     *
     * @param millis    the time in milliseconds after January 1, 1970
     */
    public TimeStamp(@NonNull Long millis) {
        mDateTime = new DateTime(millis, ISO_CHRONOLOGY)
                        .withTime(FIXED_HOUR, FIXED_MINUTES, FIXED_SECONDS, FIXED_MILLI);
    }

    /**
     * Constructs {@link TimeStamp} with a {@link DateTime}.
     * It will make sure that given {@link DateTime} is adjusted
     * to the fixed time frame for easier comparisons.
     *
     * @param dateTime  a {@link DateTime} that will be assigned
     */
    public TimeStamp(@NonNull DateTime dateTime) {
        mDateTime = new DateTime(dateTime, ISO_CHRONOLOGY)
                .withTime(FIXED_HOUR, FIXED_MINUTES, FIXED_SECONDS, FIXED_MILLI);
    }

    public DateTime getDateTime() {
        return mDateTime;
    }

    public static TimeStamp getToday() {
        return new TimeStamp(Instant.now());
    }

    @Override
    public boolean equals(Object o) {
        boolean flag = false;
        if(this == o) {
            flag = true;
        }

        if(!(o instanceof TimeStamp)) {
            return flag;
        }

        TimeStamp timeStamp = (TimeStamp) o;
        if(mDateTime.getMillis() == timeStamp.mDateTime.getMillis()) {
            flag = true;
        }
        return flag;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(mDateTime.getMillis()).hashCode();
    }

    /**
     * Method helps to produce list of time stamps that represents days on calendar.
     *
     * @param start the start date
     * @param end   the end date
     * @return  a list of dates that starts from given start time and ends on the given
     *              end time.
     */
    public static List<TimeStamp> generateDateTimes(DateTime start, DateTime end) {
        DateTime currDate = start;
        List<TimeStamp> dates = new ArrayList<>();

        while(!currDate.isAfter(end)) {
            dates.add(new TimeStamp(currDate));
            currDate = currDate.plusDays(1);
        }
        return dates;
    }
}
