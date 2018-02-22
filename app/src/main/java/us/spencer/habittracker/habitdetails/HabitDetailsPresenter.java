package us.spencer.habittracker.habitdetails;

import android.support.annotation.NonNull;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.database.HabitsRepository;
import us.spencer.habittracker.model.HabitRepetitions;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handles logic between model and view using callbacks to notify load completion.
 */
public class HabitDetailsPresenter implements HabitDetailsContract.Presenter, HabitsDataSource.LoadHabitCallback {

    @NonNull
    private HabitsDataSource mHabitsRepository;

    @NonNull
    private HabitDetailsContract.DetailsFragmentView mHabitDetailsView;

    private long mHabitId;

    public HabitDetailsPresenter(@NonNull HabitsRepository habitsRepository,
                                 @NonNull HabitDetailsContract.DetailsFragmentView habitDetailsFragment,
                                 long habitId) {
        mHabitsRepository = checkNotNull(habitsRepository);
        mHabitDetailsView = checkNotNull(habitDetailsFragment);
        mHabitId = habitId;
        mHabitDetailsView.setPresenter(this);
    }

    /**
     * Method will get the habit by specified id and pass information
     * to the calendar view to present
     *
     * @param habit the loaded {@link HabitRepetitions} from data source
     */
    public void loadHabit(HabitRepetitions habit) {
        mHabitDetailsView.showHistory(habit);
    }

    @Override
    public void onHabitLoaded(HabitRepetitions habit) {
        loadHabit(habit);
    }

    @Override
    public void onDataNotAvailable() {

    }

    public void start() {
        mHabitsRepository.getHabitById(mHabitId, this);
    }
}
