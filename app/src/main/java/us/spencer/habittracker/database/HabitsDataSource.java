package us.spencer.habittracker.database;

import android.support.annotation.NonNull;

import java.util.List;

import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.Repetition;

/**
 * Entry point for accessing habit data.
 */
public interface HabitsDataSource {

    /**
     * =======================================================================
     * CALLBACKS
     * =======================================================================
     */

    interface SaveHabitCallback {

        void onHabitSaved();

    }

    interface LoadHabitsCallback {

        void onHabitsLoaded(@NonNull List<Habit> habits);

        void onDataNotAvailable();
    }

    interface SyncCacheCallback {

        void onHabitIdGenerated(final long id, @NonNull final Habit habit);
    }

    /**
     * =======================================================================
     * DATABASE
     * =======================================================================
     */

    interface Database {

        void insertHabit(@NonNull final Habit habit,
                         @NonNull final SaveHabitCallback saveHabitCallback,
                         @NonNull final SyncCacheCallback syncCacheCallback);

        void queryAllHabits(@NonNull final LoadHabitsCallback callback);

        void deleteAllHabits();

        void insertRepetition(final long habitId,
                              @NonNull final Repetition repetition);
    }

    /**
     * =======================================================================
     * REPOSITORY (i.e the general interface for getting data)
     * =======================================================================
     */

    void insertHabit(@NonNull final Habit habit,
                     @NonNull final SaveHabitCallback saveHabitCallback);

    void queryAllHabits(@NonNull final LoadHabitsCallback loadHabitsCallback);

    void deleteAllHabits();

    void insertRepetition(final long habitId,
                          @NonNull final Repetition repetition);
}
