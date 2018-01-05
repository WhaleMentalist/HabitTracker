package us.spencer.habittracker.habits;

import android.support.annotation.NonNull;

import us.spencer.habittracker.database.HabitsDataSource;

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

    }

    public void start() {

    }
}
