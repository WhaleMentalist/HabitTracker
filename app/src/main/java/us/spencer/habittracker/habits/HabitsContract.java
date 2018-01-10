package us.spencer.habittracker.habits;

import android.support.annotation.NonNull;

import java.sql.Time;
import java.util.List;

import us.spencer.habittracker.BasePresenter;
import us.spencer.habittracker.BaseView;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;

/**
 * Specifies contract between presenter and view
 */
public interface HabitsContract {

    interface View extends BaseView<Presenter> {

        void showHabits(List<Habit> habits);

        void showAddHabit();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void addHabit();

        void addRepetition(final long habitId, @NonNull final TimeStamp timeStamp);

        void deleteRepetition(final long habitId, @NonNull final TimeStamp timeStamp);

        void loadHabits();
    }
}
