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
    @ColumnInfo(name = "habit_name")
    @NonNull
    private String mName;

    @ColumnInfo(name = "habit_desc")
    @Nullable
    private String mDescription;

    /**
     * Constructor for creating a new habit
     *
     * @param name  the name of the habit
     * @param description   the description of the habit
     */
    public Habit(@NonNull String name, @Nullable String description) {
        mName = name;
        mDescription = description;
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

        return mName.equals(habit.mName);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    @Override
    public String toString() {
        return "Habit Name: " + mName;
    }
}
