package us.spencer.habittracker.database.local;

import android.support.annotation.NonNull;

import java.util.List;

import us.spencer.habittracker.database.HabitsDAO;
import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.utility.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of data source as a database.
 * This will be a singleton design pattern.
 */
public class HabitsLocalDataSource implements HabitsDataSource {

    private static volatile HabitsLocalDataSource INSTANCE;

    private HabitsDAO mHabitsDAO;

    private AppExecutors mAppExecutors;

    /**
     * Private constructor to enforce singleton design pattern
     *
     * @param appExecutors  the executor that will run queries on seperate thread
     * @param habitsDAO  the data access object that helps with queries to database
     */
    private HabitsLocalDataSource(@NonNull AppExecutors appExecutors, HabitsDAO habitsDAO) {
        mAppExecutors = appExecutors;
        mHabitsDAO = habitsDAO;
    }

    /**
     * Creates single instance of class if necessary.
     *
     * @param appExecutors  the executor that will run I/O in background thread
     * @param habitsDAO  the data access object that helps with database access
     * @return  an instance of {@link HabitsLocalDataSource}
     */
    public static HabitsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                    @NonNull HabitsDAO habitsDAO) {
        if(INSTANCE == null) {
            synchronized (HabitsLocalDataSource.class) {
                if(INSTANCE == null) {
                    INSTANCE = new HabitsLocalDataSource(appExecutors, habitsDAO);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Method will save desired habit into database
     *
     * @param habit the habit to add
     * @param callback  the callback that will be notified of result
     */
    @Override
    public void saveHabit(@NonNull final Habit habit, @NonNull final SaveHabitCallback callback) {
        checkNotNull(habit);
        checkNotNull(callback);
        Runnable saveHabit = new Runnable() {

            @Override
            public void run() {
                mHabitsDAO.insertHabit(habit);

                /** Need to execute UI changes on main thread */
                mAppExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        callback.onHabitSaved();
                    }
                });

            }
        };
        mAppExecutors.diskIO().execute(saveHabit); /** Execute DB read on own thread */
    }

    @Override
    public void getHabits(@NonNull final LoadHabitsCallback callback) {
        checkNotNull(callback);
        Runnable loadHabits = new Runnable() {

            @Override
            public void run() {
                final List<Habit> habits = mHabitsDAO.getHabits();

                mAppExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        callback.onHabitsLoaded(habits);
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(loadHabits);
    }

    @Override
    public void deleteAllHabits() {

        Runnable deleteHabits = new Runnable() {

            @Override
            public void run() {
                mHabitsDAO.deleteHabits();
            }
        };
        mAppExecutors.diskIO().execute(deleteHabits);
    }
}