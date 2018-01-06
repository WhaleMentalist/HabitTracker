package us.spencer.habittracker.habits;

import android.support.annotation.NonNull;

import java.util.List;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;

import static com.google.common.base.Preconditions.checkNotNull;

public class HabitsPresenter implements HabitsContract.Presenter {

    @NonNull
    HabitsDataSource mHabitsRepository;

    @NonNull
    HabitsContract.View mHabitsView;

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
        mHabitsRepository.getHabits(new HabitsDataSource.LoadHabitsCallback() {

            @Override
            public void onHabitsLoaded(@NonNull List<Habit> habits) {
                mHabitsView.showHabits(habits);
            }
        });
    }

    public void start() {
        loadHabits();
    }
}
