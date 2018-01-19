package us.spencer.habittracker.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "repetitions", primaryKeys = {"time_stamp", "habit_id"},
        foreignKeys = @ForeignKey(onDelete = CASCADE, entity = Habit.class,
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

    @Ignore
    public Repetition() {}

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

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if(this == o) {
            result = true;
        }

        Repetition repetition = (Repetition) o;
        if(mTimeStamp.equals(repetition.mTimeStamp) &&
                mHabitId == repetition.mHabitId) {
            result = true;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return mTimeStamp.hashCode() + Long.valueOf(mHabitId).hashCode();
    }
}
