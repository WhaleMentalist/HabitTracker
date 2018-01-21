package us.spencer.habittracker.database.local;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import us.spencer.habittracker.database.dao.HabitRepetitionsDAO;
import us.spencer.habittracker.database.dao.HabitsDAO;
import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.database.dao.RepetitionsDAO;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.utility.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of data source as a database.
 * This will be a singleton design pattern.
 */
public class HabitsLocalDataSource implements HabitsDataSource {

    private static volatile HabitsLocalDataSource INSTANCE;

    private HabitsDAO mHabitsDAO;

    private RepetitionsDAO mRepetitionsDAO;

    private HabitRepetitionsDAO mHabitRepetitionsDAO;

    private AppExecutors mAppExecutors;

    /**
     * Private constructor to enforce singleton design pattern
     *
     * @param appExecutors  the executor that will run queries on seperate thread
     * @param habitsDAO  the data access object that helps with access to habits
     * @param repetitionsDAO the data access object that helps with access to repetitions
     */
    private HabitsLocalDataSource(@NonNull AppExecutors appExecutors,
                                  @NonNull HabitsDAO habitsDAO,
                                  @NonNull RepetitionsDAO repetitionsDAO,
                                  @NonNull HabitRepetitionsDAO habitRepetitionsDAO) {
        mAppExecutors = appExecutors;
        mHabitsDAO = habitsDAO;
        mRepetitionsDAO = repetitionsDAO;
        mHabitRepetitionsDAO = habitRepetitionsDAO;
    }

    /**
     * Creates single instance of class if necessary.
     *
     * @param appExecutors  the executor that will run I/O in background thread
     * @param habitsDAO  the data access object that helps with access to habits
     * @param repetitionsDAO the data access object that helps with access to repetitions
     * @param habitRepetitionsDAO the data access object that helps to join the habits and
     *                            repetitions table
     * @return  an instance of {@link HabitsLocalDataSource}
     */
    public static HabitsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                    @NonNull HabitsDAO habitsDAO,
                                                    @NonNull RepetitionsDAO repetitionsDAO,
                                                    @NonNull HabitRepetitionsDAO habitRepetitionsDAO) {
        if(INSTANCE == null) {
            synchronized (HabitsLocalDataSource.class) {
                if(INSTANCE == null) {
                    INSTANCE = new HabitsLocalDataSource(appExecutors,
                            habitsDAO,
                            repetitionsDAO,
                            habitRepetitionsDAO);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Method will save desired habit into database. It will
     * replace a duplicate habit.
     *
     * @param habit the habit to add
     * @param saveHabitCallback  the callback that will be notified of result
     */
    @Override
    public long insertHabit(@NonNull final Habit habit,
                            @NonNull final HabitsDataSource.SaveHabitCallback saveHabitCallback)
                                                                        throws InterruptedException,
                                                                                ExecutionException {
        checkNotNull(habit);
        checkNotNull(saveHabitCallback);

        Callable<Long> insertHabit = new Callable<Long>() {

            @Override
            public Long call() throws Exception {
                long generatedId = mHabitsDAO.insertHabit(habit);
                mAppExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        saveHabitCallback.onHabitSaved();}
                });
                return generatedId;
            }
        };
        return mAppExecutors.diskIO().submit(insertHabit).get();
    }

    @Override
    public void queryAllHabits(@NonNull final HabitsDataSource.LoadHabitsCallback callback) {
        checkNotNull(callback);
        Runnable queryAllHabits = new Runnable() {

            @Override
            public void run() {
                final List<HabitRepetitions> habits = mHabitRepetitionsDAO.getHabitsWithRepetitions();
                if(habits.isEmpty()) {
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataNotAvailable();
                        }
                    });
                }
                else {
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onHabitsLoaded(habits);
                        }
                    });
                }
            }
        };
        mAppExecutors.diskIO().execute(queryAllHabits);
    }

    @Override
    public void deleteAllHabits() {
        Runnable deleteAllHabits = new Runnable() {
            @Override
            public void run() {
                mHabitsDAO.deleteHabits();
            }
        };
        mAppExecutors.diskIO().execute(deleteAllHabits);
    }

    @Override
    public void insertRepetition(final long habitId, @NonNull final Repetition repetition) {
        checkNotNull(repetition);
        Runnable insertRepetition = new Runnable() {
            @Override
            public void run() {
                mRepetitionsDAO.insertRepetition(repetition);
            }
        };
        mAppExecutors.diskIO().execute(insertRepetition);
    }

    @Override
    public void deleteRepetition(final long habitId, @NonNull final Repetition repetition) {
        checkNotNull(repetition);
        Runnable deleteRepetition = new Runnable() {
            @Override
            public void run() {
                mRepetitionsDAO.deleteRepetition(repetition);
            }
        };
        mAppExecutors.diskIO().execute(deleteRepetition);
    }

    @Override
    public void getHabitById(final long habitId, @NonNull final LoadHabitCallback callback) {
        checkNotNull(callback);
        Runnable findHabitById = new Runnable() {
            @Override
            public void run() {
                final HabitRepetitions habit = mHabitRepetitionsDAO.getHabitWithRepetitions(habitId);
                if(habit == null) {
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDataNotAvailable();
                        }
                    });
                }
                else {
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onHabitLoaded(habit);
                        }
                    });
                }
            }
        };
        mAppExecutors.diskIO().execute(findHabitById);
    }
}
