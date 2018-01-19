package us.spencer.habittracker.habitdetails;

import android.support.annotation.NonNull;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.database.HabitsRepository;
import us.spencer.habittracker.model.HabitRepetitions;

import static com.google.common.base.Preconditions.checkNotNull;

public class HabitDetailsPresenter implements HabitDetailsContract.Presenter {

    @NonNull
    private HabitsDataSource mHabitsRepository;

    @NonNull
    private HabitDetailsContract.CalendarFragmentView mHabitDetailsCalendarView;

    private long mHabitId;

    public HabitDetailsPresenter(@NonNull HabitsRepository habitsRepository,
                                 @NonNull HabitDetailsContract.CalendarFragmentView habitDetailsFragment,
                                 long habitId) {
        mHabitsRepository = checkNotNull(habitsRepository);
        mHabitDetailsCalendarView = checkNotNull(habitDetailsFragment);
        mHabitId = habitId;
        mHabitDetailsCalendarView.setPresenter(this);
    }

    /**
     * Method will get the habit by specified id and pass information
     * to the calendar view to present
     *
     * @param habitId   the id that will be searched for in the database
     */
    public void loadHabit(final long habitId) {
        HabitRepetitions habit = mHabitsRepository.getHabitById(habitId);
        mHabitDetailsCalendarView.showHistory(habit);
    }

    public void start() {
        loadHabit(mHabitId);
    }
}
