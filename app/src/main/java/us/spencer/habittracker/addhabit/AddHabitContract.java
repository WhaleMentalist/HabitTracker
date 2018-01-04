package us.spencer.habittracker.addhabit;

import us.spencer.habittracker.BasePresenter;
import us.spencer.habittracker.BaseView;

/**
 * Specify a contract (i.e interface) between the
 * presenter and view when adding a habit
 */
public interface AddHabitContract {

    interface View extends BaseView<Presenter> {

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void addHabit(String title, String description);
    }
}
