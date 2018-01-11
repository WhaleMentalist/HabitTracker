package us.spencer.habittracker.habitdetails;

import us.spencer.habittracker.BasePresenter;
import us.spencer.habittracker.BaseView;

/**
 * Specify a contract (i.e interface) between the
 * presenter and view when viewing a habit
 */
public interface HabitDetailsContract {

    interface View extends BaseView<Presenter> {

        void showHabitsList();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {


    }
}
