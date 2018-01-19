package us.spencer.habittracker.habits;

import android.support.annotation.NonNull;

import java.util.List;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;
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

    @Override
    public void addHabit() {
        mHabitsView.showAddHabit();
    }

    @Override
    public void addRepetition(final long habitId, @NonNull final TimeStamp timeStamp) {
        final Repetition repetition = new Repetition(timeStamp, habitId);
        mHabitsRepository.insertRepetition(habitId, repetition);
    }

    @Override
    public void loadHabitDetails(final Habit habit) {
        mHabitsView.showHabitDetails(habit);
    }

    @Override
    public void loadHabits() {
        EspressoCountingIdlingResource.getIdlingResource().increment();
        mHabitsRepository.queryAllHabits(new HabitsDataSource.LoadHabitsCallback() {

            @Override
            public void onHabitsLoaded(@NonNull List<HabitRepetitions> habits) {
                EspressoCountingIdlingResource.getIdlingResource().decrement();
                mHabitsView.showHabits(habits);
            }

            @Override
            public void onDataNotAvailable() {
                EspressoCountingIdlingResource.getIdlingResource().decrement();
            }

        });
    }

    @Override
    public void deleteRepetition(final long habitId, @NonNull final TimeStamp timeStamp) {
        final Repetition repetition = new Repetition(timeStamp, habitId);
        mHabitsRepository.deleteRepetition(habitId, repetition);
    }

    @Override
    public void start() {
        loadHabits();
    }
}
