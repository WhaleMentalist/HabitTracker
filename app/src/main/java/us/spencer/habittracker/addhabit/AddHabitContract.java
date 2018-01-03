package us.spencer.habittracker.addhabit;

import us.spencer.habittracker.BasePresenter;
import us.spencer.habittracker.BaseView;

/**
 * Specify a contract (i.e interface) between the
 * presenter and view when adding a habit
 */
public class AddHabitContract {

    public interface View extends BaseView<Presenter> {

        boolean isActive();
    }

    public interface Presenter extends BasePresenter {


    }
}
