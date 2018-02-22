package us.spencer.habittracker.habitdetails;

import us.spencer.habittracker.BasePresenter;
import us.spencer.habittracker.BaseView;
import us.spencer.habittracker.model.HabitRepetitions;

/**
 * Specify a contract (i.e interface) between the
 * presenter and view when viewing a habit
 */
public interface HabitDetailsContract {

    interface DetailsFragmentView extends BaseView<Presenter> {

        void showHistory(HabitRepetitions habit);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadHabit(HabitRepetitions habit);
    }
}
