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

        void onDuplicateHabit();
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

        void insertHabitNoReplace(@NonNull final Habit habit,
                                  @NonNull final SaveHabitCallback saveHabitCallback,
                                  @NonNull final SyncCacheCallback syncCacheCallback);

        void insertHabitReplace(@NonNull final Habit habit,
                                @NonNull final SaveHabitCallback saveHabitCallback,
                                @NonNull final SyncCacheCallback syncCacheCallback);

        void queryAllHabits(@NonNull final LoadHabitsCallback callback);

        void deleteAllHabits();
    }

    /**
     * =======================================================================
     * REPOSITORY (i.e the general interface for getting data)
     * =======================================================================
     */

    void saveHabitNoReplace(@NonNull final Habit habit,
                            @NonNull final SaveHabitCallback saveHabitCallback);

    void saveHabitReplace(@NonNull final Habit habit,
                          @NonNull final SaveHabitCallback saveHabitCallback);

    void retrieveAllHabits(@NonNull final LoadHabitsCallback loadHabitsCallback);

    void removeAllHabits();
}
