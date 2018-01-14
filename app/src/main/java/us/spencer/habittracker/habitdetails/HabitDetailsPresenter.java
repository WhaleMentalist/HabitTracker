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
    private HabitDetailsContract.View mHabitDetailsView;

    public HabitDetailsPresenter(@NonNull HabitsRepository habitsRepository,
                                 @NonNull HabitDetailsContract.View habitDetailsFragment) {
        mHabitsRepository = checkNotNull(habitsRepository);
        mHabitDetailsView = checkNotNull(habitDetailsFragment);
    }

    public void loadHabit(final long habitId) {

    }

    public void start() {

    }
}
