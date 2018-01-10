package us.spencer.habittracker.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import java.sql.Time;

@Entity(tableName = "repetitions", primaryKeys = {"time_stamp", "habit_id"},
        foreignKeys = @ForeignKey(entity = Habit.class,
                parentColumns = "id",
                childColumns = "habit_id"),
        indices = {@Index("habit_id")})
public class Repetition {

    @ColumnInfo(name = "time_stamp")
    @NonNull
    private TimeStamp mTimeStamp;

    @ColumnInfo(name = "habit_id")
    @NonNull
    private long mHabitId;

    /**
     * Constructor for entity
     *
     * @param timeStamp the time stamp of completion
     * @param habitId the habit that was completed
     */
    public Repetition(@NonNull TimeStamp timeStamp, @NonNull long habitId) {
        mTimeStamp = timeStamp;
        mHabitId = habitId;
    }

    @NonNull
    public TimeStamp getTimeStamp() {
        return mTimeStamp;
    }

    @NonNull
    public long getHabitId() {
        return mHabitId;
    }

    public void setTimeStamp(@NonNull final TimeStamp timeStamp) {
        mTimeStamp = timeStamp;
    }

    public void setHabitId(final long habitId) {
        mHabitId = habitId;
    }
}
