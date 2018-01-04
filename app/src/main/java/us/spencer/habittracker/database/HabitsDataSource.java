package us.spencer.habittracker.database;

import android.support.annotation.NonNull;

import us.spencer.habittracker.model.Habit;

/**
 * Entry point for accessing habit data.
 */
public interface HabitsDataSource {

    interface SaveHabitCallback {

        void onHabitSaved();

    }

    void saveHabit(@NonNull Habit habit, SaveHabitCallback callback);
}
