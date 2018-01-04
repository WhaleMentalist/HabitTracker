package us.spencer.habittracker.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import java.util.Objects;
import java.util.UUID;

/**
 * The habit the user wants to track
 */
@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "habit_id")
    private final Long mId;

    @ColumnInfo(name = "habit_name")
    @Nullable
    private String mName;

    @ColumnInfo(name = "habit_desc")
    @Nullable
    private String mDescription;

    /**
     * Constructor for creating a new habit
     *
     * @param id   the id of the habit
     * @param name  the name of the habit
     * @param description   the description of the habit
     */
    public Habit(@NonNull Long id, @Nullable String name, @Nullable String description) {
        mId = id;
        mName = name;
        mDescription = description;
    }

    @NonNull
    public Long getId() {
        return mId;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public void setName(@Nullable String name) {
        mName = name;
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName) &&
                Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        if(o == null || getClass() != o.getClass()) return false;

        Habit habit = (Habit) o;

        return mId.equals(habit.mId);
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public String toString() {
        return "Habit Name: " + mName;
    }
}
