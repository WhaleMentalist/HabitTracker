package us.spencer.habittracker.database;

import android.support.annotation.NonNull;

import java.util.List;

import us.spencer.habittracker.model.Habit;

/**
 * Entry point for accessing habit data.
 */
public interface HabitsDataSource {

    interface SaveHabitCallback {

        void onHabitSaved();

        void onDuplicateHabit();

    }

    interface SaveRepetitionCallback {

        void onRepetitionSaved();
    }

    interface LoadHabitsCallback {

        void onHabitsLoaded(@NonNull List<Habit> habits);

        void onDataNotAvailable();
    }

    void saveHabitNoReplace(@NonNull final Habit habit, @NonNull SaveHabitCallback callback);

    void saveHabitReplace(@NonNull final Habit habit, @NonNull SaveHabitCallback callback);

    void getHabits(@NonNull final LoadHabitsCallback callback);

    void deleteAllHabits();

}
