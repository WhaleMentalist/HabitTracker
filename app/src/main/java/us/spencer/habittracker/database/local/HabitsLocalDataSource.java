package us.spencer.habittracker.database.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import us.spencer.habittracker.database.dao.HabitsDAO;
import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.utility.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of data source as a database.
 * This will be a singleton design pattern.
 */
public class HabitsLocalDataSource implements HabitsDataSource.Database {

    private static volatile HabitsLocalDataSource INSTANCE;

    private HabitsDAO mHabitsDAO;

    private AppExecutors mAppExecutors;

    /**
     * Private constructor to enforce singleton design pattern
     *
     * @param appExecutors  the executor that will run queries on seperate thread
     * @param habitsDAO  the data access object that helps with access to habits
     */
    private HabitsLocalDataSource(@NonNull AppExecutors appExecutors,
                                  @NonNull HabitsDAO habitsDAO) {
        mAppExecutors = appExecutors;
        mHabitsDAO = habitsDAO;
    }

    /**
     * Creates single instance of class if necessary.
     *
     * @param appExecutors  the executor that will run I/O in background thread
     * @param habitsDAO  the data access object that helps with access to habits
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
     * @param saveHabitCallback  the callback that will be notified of result
     */
    @Override
    public void insertHabitNoReplace(@NonNull final Habit habit,
                                     @NonNull final HabitsDataSource.SaveHabitCallback saveHabitCallback,
                                     @Nullable final HabitsDataSource.SyncCacheCallback syncCacheCallback) {
        checkNotNull(habit);
        checkNotNull(saveHabitCallback);
        Runnable saveHabit = new Runnable() {

            @Override
            public void run() {
                Habit result = mHabitsDAO.getHabitById(habit.getId());

                if(result != null) {
                    mAppExecutors.mainThread().execute(new Runnable() {

                        @Override
                        public void run() {
                            saveHabitCallback.onDuplicateHabit();
                        }
                    });
                }
                else {
                    long generatedId = mHabitsDAO.insertHabit(habit);
                    syncCacheCallback.onHabitIdGenerated(generatedId, habit);

                    mAppExecutors.mainThread().execute(new Runnable() {

                        @Override
                        public void run() {
                            saveHabitCallback.onHabitSaved();
                        }
                    });
                }
            }
        };
        mAppExecutors.diskIO().execute(saveHabit);
    }

    /**
     * Method will save desired habit into database. It will
     * replace duplicate habit
     *
     * @param habit the habit to add
     * @param saveHabitCallback  the callback that will be notified when action performed
     */
    @Override
    public void insertHabitReplace(@NonNull final Habit habit,
                                   @NonNull final HabitsDataSource.SaveHabitCallback saveHabitCallback,
                                   @Nullable final HabitsDataSource.SyncCacheCallback syncCacheCallback) {
        checkNotNull(habit);
        checkNotNull(saveHabitCallback);
        Runnable saveHabit = new Runnable() {

            @Override
            public void run() {
                long generatedId = mHabitsDAO.insertHabit(habit);
                syncCacheCallback.onHabitIdGenerated(generatedId, habit);

                mAppExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        saveHabitCallback.onHabitSaved();
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(saveHabit);
    }

    @Override
    public void queryAllHabits(@NonNull final HabitsDataSource.LoadHabitsCallback callback) {
        checkNotNull(callback);
        Runnable queryAllHabits = new Runnable() {

            @Override
            public void run() {
                final List<Habit> habits = mHabitsDAO.getHabits();
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
}
