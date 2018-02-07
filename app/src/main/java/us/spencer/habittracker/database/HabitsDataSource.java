package us.spencer.habittracker.database;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutionException;

import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
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

        void onHabitsLoaded(@NonNull List<HabitRepetitions> habits);

        void onDataNotAvailable();
    }

    interface LoadHabitCallback {

        void onHabitLoaded(HabitRepetitions habit);

        void onDataNotAvailable();
    }

    interface DeleteHabitCallback {

        void onHabitDeleted();

        void onHabitNotFound();
    }

    interface SaveRepetitionCallback {

        void onRepetitionSaved();
    }

    /**
     * =======================================================================
     * REPOSITORY / DATABASE (i.e the general interface for getting data)
     * =======================================================================
     */

    long insertHabit(@NonNull final Habit habit,
                     @NonNull final SaveHabitCallback saveHabitCallback)
            throws InterruptedException, ExecutionException;

    void queryAllHabits(@NonNull final LoadHabitsCallback loadHabitsCallback);

    void deleteAllHabits();

    void deleteHabitById(final long habitId);

    void insertRepetition(final long habitId,
                          @NonNull final Repetition repetition);

    void deleteRepetition(final long habitId,
                          @NonNull final Repetition repetition);

    void getHabitById(final long habitId,
                      @NonNull LoadHabitCallback callback);
}
