package us.spencer.habittracker.addhabit;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutionException;

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
    public void start() {
        /* Does nothing as of now - 1/20/2018 */
    }

    @Override
    public void addHabit(@NonNull final String title, @NonNull final String description) {
        Habit habit = new Habit(title, description);

        if(habit.isEmpty()) {
            mAddHabitView.showEmptyHabitError();
        }
        else {
            try {
                mHabitsRepository.insertHabit(habit, this);
            }
            catch(InterruptedException e) {
                mAddHabitView.showAddHabitError();
            }
            catch (ExecutionException e) {
                mAddHabitView.showAddHabitError();
            }
        }
    }

    @Override
    public void onHabitSaved() {
        mAddHabitView.showHabitsList();
    }

}
