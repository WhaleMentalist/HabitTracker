package us.spencer.habittracker.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Basically this can be thought of as a natural join between the 'habits' and 'repetitions'
 * table. It will give a list of repetitions that have occurred for the habit.
 *
 * Article: https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a
 */
public class HabitRepetitions {

    @Embedded
    private Habit mHabit;

    @Relation(parentColumn = "id", entityColumn = "habit_id")
    private Set<Repetition> mRepetitions;

    public HabitRepetitions() { }

    @Ignore
    public HabitRepetitions(@NonNull final Habit habit) {
        mHabit = habit;
        mRepetitions = new HashSet<>();
    }

    @NonNull
    public Habit getHabit() {
        return mHabit;
    }

    @NonNull
    public Set<Repetition> getRepetitions() {
        return mRepetitions;
    }

    public void setHabit(@NonNull final Habit habit) {
        mHabit = habit;
    }

    public void setRepetitions(@NonNull final Set<Repetition> repetitions) {
        mRepetitions = repetitions;
    }
}
