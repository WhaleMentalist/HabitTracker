package us.spencer.habittracker.model;

import android.animation.ObjectAnimator;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

/**
 * The habit the user wants to track
 */
@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId = 0;

    @ColumnInfo(name = "habit_name")
    @NonNull
    private String mName;

    @ColumnInfo(name = "habit_desc")
    @NonNull
    private String mDescription;

    @Ignore
    public Habit(long id, @NonNull String name, @NonNull String description) {
        mId = id;
        mName = name;
        mDescription = description;
    }

    public Habit(@NonNull String name, @NonNull String description) {
        mName = name;
        mDescription = description;
    }

    public long getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    public void setDescription(@NonNull String description) {
        mDescription = description;
    }

    /**
     * Check if habit contains 'blank' or 'null'
     * entries.
     *
     * @return whether habit has no missing field
     */
    @Ignore
    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName) ||
                Strings.isNullOrEmpty(mDescription);
    }

    @Ignore
    public boolean equals(Object o) {
        boolean result = false;
        if(this == o) {
            result = true;
        }

        Habit habit = (Habit) o;
        if(mId == habit.mId &&
                mName.equals(habit.mName) &&
                mDescription.equals(habit.mDescription)) {
            result = true;
        }
        return result;
    }
}
