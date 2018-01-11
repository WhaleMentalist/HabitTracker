package us.spencer.habittracker.model;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;

/**
 * Class represents an instant in time in which a
 * particular event occurred. It is focused on the
 * date of the event for now. It will fix the time
 * to a specified amount. In this case three in the
 * afternoon.
 */
public class TimeStamp {

    /**
     * Represents 3 in the afternoon
     */
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
        mDateTime = new DateTime(instant)
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
        mDateTime = new DateTime(millis)
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
}
