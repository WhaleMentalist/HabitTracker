package us.spencer.habittracker.habits;

import java.util.List;

import us.spencer.habittracker.BasePresenter;
import us.spencer.habittracker.BaseView;
import us.spencer.habittracker.model.Habit;

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

        void loadHabits();
    }
}
