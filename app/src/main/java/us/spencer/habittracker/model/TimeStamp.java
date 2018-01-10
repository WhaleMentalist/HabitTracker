package us.spencer.habittracker.model;

import android.support.annotation.NonNull;

import org.joda.time.Instant;

/**
 * Class represents an instant in time in which a
 * particular event occurred.
 */
public class TimeStamp {

    /**
     * The instant in which the event occurred
     */
    private final Instant mInstant;

    /**
     * Constructs a {@link TimeStamp} containing the
     * {@link Instant} that represents the time the
     * event occurred.
     *
     * @param instant   the time the event occurred
     */
    public TimeStamp(@NonNull Instant instant) {
        mInstant = instant;
    }

    /**
     * Constructs a {@link TimeStamp} containing the {@link Instant}
     * at the specified milliseconds
     *
     * @param millis    the time in milliseconds after January 1, 1970
     */
    public TimeStamp(@NonNull Long millis) {mInstant = new Instant(millis); }

    public Instant getInstant() {
        return mInstant;
    }
}
