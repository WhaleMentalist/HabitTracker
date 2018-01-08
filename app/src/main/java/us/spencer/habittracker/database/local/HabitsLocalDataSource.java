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
     * Method will save desired habit into database. It will
     * not replace duplicate habits
     *
     * @param habit the habit to add
     * @param callback  the callback that will be notified of result
     */
    @Override
    public void saveHabitNoReplace(@NonNull final Habit habit, @NonNull final SaveHabitCallback callback) {
        checkNotNull(habit);
        checkNotNull(callback);
        Runnable saveHabit = new Runnable() {

            @Override
            public void run() {
                Habit result = mHabitsDAO.getHabitById(habit.getName());

                if(result != null) { /** Duplicate found*/

                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDuplicateHabit(); /** Notify that duplicate habit was found */
                        }
                    });
                }
                else {
                    mHabitsDAO.insertHabit(habit);

                    /** Need to execute UI changes on main thread */
                    mAppExecutors.mainThread().execute(new Runnable() {

                        @Override
                        public void run() {
                            callback.onHabitSaved(); /** Notify that habit was saved */
                        }
                    });
                }
            }
        };
        mAppExecutors.diskIO().execute(saveHabit); /** Execute DB read on own thread */
    }

    /**
     * Method will save desired habit into database. It will
     * replace duplicate habit
     *
     * @param habit the habit to add
     * @param callback  the callback that will be notified when action performed
     */
    @Override
    public void saveHabitReplace(@NonNull final Habit habit, @NonNull final SaveHabitCallback callback) {
        checkNotNull(habit);
        checkNotNull(callback);

        Runnable saveHabit = new Runnable() {

            @Override
            public void run() {
                mHabitsDAO.insertHabit(habit);

                mAppExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        callback.onHabitSaved(); /** Notify habit was saved */
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(saveHabit);
    }

    /**
     * Method will retrieve all habits from local database
     *
     * @param callback  notifies when retrieval action is finished or runs into error
     */
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

                        if(habits.isEmpty()) {
                            callback.onDataNotAvailable();
                        }
                        else {
                            callback.onHabitsLoaded(habits);
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(loadHabits);
    }

    /**
     * Method will delete all the habits from the database
     *
     * TODO: Add callback to notify interested parties for better coordination
     */
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
