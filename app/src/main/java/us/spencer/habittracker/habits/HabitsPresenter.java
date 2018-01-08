package us.spencer.habittracker.habits;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.idling.CountingIdlingResource;

import java.util.List;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.utility.EspressoCountingIdlingResource;

import static com.google.common.base.Preconditions.checkNotNull;

public class HabitsPresenter implements HabitsContract.Presenter {

    @NonNull
    private HabitsDataSource mHabitsRepository;

    @NonNull
    private HabitsContract.View mHabitsView;


    public HabitsPresenter(@NonNull HabitsDataSource habitsRepository,
                           @NonNull HabitsContract.View habitsView) {
        mHabitsRepository = checkNotNull(habitsRepository);
        mHabitsView = checkNotNull(habitsView);
        mHabitsView.setPresenter(this);
    }

    public void addHabit() {
        mHabitsView.showAddHabit();
    }

    public void loadHabits() {
        EspressoCountingIdlingResource.getIdlingResource().increment();
        mHabitsRepository.getHabits(new HabitsDataSource.LoadHabitsCallback() {

            @Override
            public void onHabitsLoaded(@NonNull List<Habit> habits) {
                EspressoCountingIdlingResource.getIdlingResource().decrement();
                mHabitsView.showHabits(habits);
            }

            @Override
            public void onDataNotAvailable() {
                EspressoCountingIdlingResource.getIdlingResource().decrement();
            }

        });
    }

    public void start() {
        loadHabits();
    }
}
