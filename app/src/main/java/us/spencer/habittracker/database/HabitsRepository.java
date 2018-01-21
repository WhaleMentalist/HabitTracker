package us.spencer.habittracker.database;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation that allows access into database containing
 * habits. For now, we will use local storage. Later, a remote
 * may be implemented for more interesting features
 * (i.e sharing completion of habit or habit list).
 */
public class HabitsRepository implements HabitsDataSource {

    private static final Logger LOGGER = Logger.getLogger(HabitsRepository.class.getName());

    public static final long SQL_INSERTION_FAIL = -1;

    private static HabitsRepository INSTANCE  = null;

    private final HabitsDataSource mHabitsLocalDataSource;

    @VisibleForTesting
    boolean isCacheSync = false;

    @VisibleForTesting
    Map<Long, HabitRepetitions> mCachedHabits;

    /**
     * Private constructor to enforce singleton design pattern
     *
     * @param habitsLocalDataSource the local database on device
     */
    private HabitsRepository(@NonNull HabitsDataSource habitsLocalDataSource) {
        mHabitsLocalDataSource = habitsLocalDataSource;
        mCachedHabits = new LinkedHashMap<>();
    }

    /**
     * Return single instance of class.
     *
     * @param habitsLocalDataSource the local database on device
     *
     * @return  the {@link HabitsRepository} instance
     */
    public static HabitsRepository getInstance(HabitsDataSource habitsLocalDataSource) {
        if(INSTANCE == null) {
            LOGGER.log(Level.FINE, "Creating repository instance");
            INSTANCE = new HabitsRepository(habitsLocalDataSource);
        }

        return INSTANCE;
    }

    /**
     * If cache is out of date or 'null' and we load from local storage.
     * Then we need to fill it with the data we retrieved from local storage.
     *
     * @param habits    the habits retrieved from storage
     */
    private void refreshCache(@NonNull List<HabitRepetitions> habits) {
        LOGGER.log(Level.FINE, "Refreshing cache");
        checkNotNull(habits);
        mCachedHabits.clear();
        for(HabitRepetitions habit : habits) {
            mCachedHabits.put(habit.getHabit().getId(), habit);
        }
        isCacheSync = true;
    }

    /**
     * Used to force {@link #getInstance(HabitsDataSource)} to create a new instance
     * when called again. NOTE: Useful for testing.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Method saves habit in the database, as well as the 'cache'
     * It will replace if duplicate is found
     *
     * @param habit the habit to save in DB
     * @param saveHabitCallback  the callback that will be used to notify save state
     *
     * @throws InterruptedException execution of database transaction was interrupted
     * @throws ExecutionException   the execution of the database transaction failed
     *
     * @return  the generated id of the habit from the database, it delimits a failed operation
     *          if -1 is returned
     */
    @Override
    public long insertHabit(@NonNull final Habit habit,
                            @NonNull final HabitsDataSource.SaveHabitCallback saveHabitCallback)
                                                                        throws InterruptedException,
                                                                                ExecutionException {
        checkNotNull(habit);
        long generatedId = mHabitsLocalDataSource.insertHabit(habit, saveHabitCallback);
        if(generatedId != SQL_INSERTION_FAIL) {
            LOGGER.log(Level.FINE, "Adding habit to cache with id: {0}", generatedId);
            habit.setId(generatedId);
            HabitRepetitions habitRepetitions = new HabitRepetitions();
            habitRepetitions.setHabit(habit);
            habitRepetitions.setRepetitions(new HashSet<Repetition>());
            mCachedHabits.put(generatedId, habitRepetitions);
        }
        return generatedId;
    }

    /**
     * Method will retrieve all habits from database or cache.
     * Ideally, the cache will contain the information allowing
     * for fast retrieval and less I/O operations.
     *
     * @param loadHabitsCallback  the callback that will be used to notify when habits are retrieved
     */
    @Override
    public void queryAllHabits(@NonNull final HabitsDataSource.LoadHabitsCallback loadHabitsCallback) {
        checkNotNull(loadHabitsCallback);
        if(isCacheSync) {
            LOGGER.log(Level.FINE, "Accessing cache for habits");
            loadHabitsCallback.onHabitsLoaded(new ArrayList<>(mCachedHabits.values()));
        }
        else {
            mHabitsLocalDataSource.queryAllHabits(new HabitsDataSource.LoadHabitsCallback() {

                @Override
                public void onHabitsLoaded(@NonNull List<HabitRepetitions> habits) {
                    LOGGER.log(Level.FINE, "Accessing local database for habits");
                    refreshCache(habits);
                    LOGGER.log(Level.FINE, "Issuing 'onHabitsLoaded' callback");
                    loadHabitsCallback.onHabitsLoaded(habits);
                }

                @Override
                public void onDataNotAvailable() {
                    LOGGER.log(Level.FINE, "Issuing 'onDataNotAvailable callback");
                    loadHabitsCallback.onDataNotAvailable();
                }
            });
        }
    }

    /**
     * Method will delete all habits from database and the cache
     */
    @Override
    public void deleteAllHabits() {
        mHabitsLocalDataSource.deleteAllHabits();
        LOGGER.log(Level.FINE, "Deleting all habits from cache");
        mCachedHabits.clear();
    }

    @Override
    public void deleteHabitById(final long habitId) {
        mHabitsLocalDataSource.deleteHabitById(habitId);
        mCachedHabits.remove(habitId);
    }

    /**
     * Method inserts a repetition into database. The repetition
     * has a key to the habit that was completed.
     *
     * @param habitId   the id of habit that had repetition occur
     * @param repetition    the repetition with timestamp inside
     */
    @Override
    public void insertRepetition(final long habitId, @NonNull final Repetition repetition) {
        LOGGER.log(Level.FINE, "Inserting repetition");
        mHabitsLocalDataSource.insertRepetition(habitId, repetition);
        HabitRepetitions habitRepetitions = mCachedHabits.get(habitId);
        if(habitRepetitions != null) {
            habitRepetitions.getRepetitions().add(repetition);
        }
    }

    /**
     * Method deletes repetition to associated habit.
     *
     * @param habitId   the id of habit
     * @param repetition    the repetition that will be removed
     */
    @Override
    public void deleteRepetition(final long habitId, @NonNull final Repetition repetition) {
        LOGGER.log(Level.FINE, "Deleting repetition");
        mHabitsLocalDataSource.deleteRepetition(habitId, repetition);
        HabitRepetitions habitRepetitions = mCachedHabits.get(habitId);
        if(habitRepetitions != null) {
            habitRepetitions.getRepetitions().remove(repetition);
        }
    }

    /**
     * Method retrieves {@link HabitRepetitions} from either cache or database.
     * It will use cache if it is in sync due to speed, otherwise it will access
     * database and run callback when finished.
     *
     * @param habitId   the id of habit to search
     * @param callback  the callback to notify interested code
     */
    @Override
    public void getHabitById(final long habitId, @NonNull final LoadHabitCallback callback) {
        checkNotNull(callback);
        HabitRepetitions habit;

        if(isCacheSync) {
            LOGGER.log(Level.FINE, "Getting habit from cache");
            habit = mCachedHabits.get(habitId);
            callback.onHabitLoaded(habit);
        }
        else {
            mHabitsLocalDataSource.getHabitById(habitId, new LoadHabitCallback() {

                @Override
                public void onHabitLoaded(HabitRepetitions habit) {
                    LOGGER.log(Level.FINE, "Got habit from local database");
                    mCachedHabits.put(habit.getHabit().getId(), habit); /* Sync cache */
                    callback.onHabitLoaded(habit);
                }

                @Override
                public void onDataNotAvailable() {
                    LOGGER.log(Level.FINE, "Habit not found in local database");
                    callback.onDataNotAvailable();
                }
            });
        }
    }
}
