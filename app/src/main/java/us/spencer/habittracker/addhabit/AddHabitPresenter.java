package us.spencer.habittracker.addhabit;

import android.support.annotation.NonNull;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddHabitPresenter implements AddHabitContract.Presenter,
        HabitsDataSource.SaveHabitCallback {

    @NonNull
    private final HabitsDataSource mHabitsRepository;

    @NonNull
    private final AddHabitContract.View mAddHabitView;

    public AddHabitPresenter(@NonNull HabitsDataSource habitsRepository,
                             @NonNull AddHabitContract.View addHabitView) {
        mHabitsRepository = checkNotNull(habitsRepository);
        mAddHabitView = checkNotNull(addHabitView);
        mAddHabitView.setPresenter(this);
    }

    @Override
    public void start() {}

    @Override
    public void addHabit(String title, String description) {
        Habit habit = new Habit(title, description);
        mHabitsRepository.saveHabit(habit, this);
    }

    @Override
    public void onHabitSaved() {
        mAddHabitView.showToast();
    }
}
