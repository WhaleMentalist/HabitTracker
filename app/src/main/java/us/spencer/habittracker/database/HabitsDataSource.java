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

        void onHabitIdGenerated(long id, @NonNull final Habit habit);
    }

    /**
     * =======================================================================
     * DATABASE
     * =======================================================================
     */

    interface Database {

        void insertHabitNoReplace(@NonNull Habit habit,
                                  @NonNull SaveHabitCallback callback,
                                  @NonNull SyncCacheCallback cacheCallback);

        void insertHabitReplace(@NonNull Habit habit,
                                @NonNull SaveHabitCallback callback,
                                @NonNull SyncCacheCallback cacheCallback);

        void queryAllHabits(@NonNull LoadHabitsCallback callback);

        void deleteAllHabits();
    }

    /**
     * =======================================================================
     * REPOSITORY (i.e the general interface for getting data)
     * =======================================================================
     */

    void saveHabitNoReplace(@NonNull Habit habit,
                            @NonNull SaveHabitCallback callback);

    void saveHabitReplace(@NonNull Habit habit,
                          @NonNull SaveHabitCallback callback);

    void retrieveAllHabits(@NonNull LoadHabitsCallback callback);

    void removeAllHabits();
}
