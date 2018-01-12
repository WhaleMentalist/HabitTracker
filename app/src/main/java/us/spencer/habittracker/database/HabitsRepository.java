package us.spencer.habittracker.database;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.sql.SQLException;
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

    private static final Logger LOGGER = Logger.getLogger("HabitsRepository");

    private static final int SQL_INSERTION_FAIL = -1;

    private static HabitsRepository INSTANCE  = null;

    private final HabitsDataSource.Database mHabitsLocalDataSource;

    @VisibleForTesting
    Map<Long, HabitRepetitions> mCachedHabits;

    /**
     * Private constructor to enforce singleton design pattern
     *
     * @param habitsLocalDataSource the local database on device
     */
    private HabitsRepository(@NonNull HabitsDataSource.Database habitsLocalDataSource) {
        mHabitsLocalDataSource = habitsLocalDataSource;
    }

    /**
     * Return single instance of class.
     *
     * @param habitsLocalDataSource the local database on device
     *
     * @return  the {@link HabitsRepository} instance
     */
    public static HabitsRepository getInstance(HabitsDataSource.Database habitsLocalDataSource) {
        if(INSTANCE == null) {
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
        checkNotNull(habits);

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        mCachedHabits.clear();

        for(HabitRepetitions habit : habits) {
            mCachedHabits.put(habit.getHabit().getId(), habit);
        }
    }

    /**
     * Used to force {@link #getInstance(HabitsDataSource.Database)} to create a new instance
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
     * @param saveHabitCallback  the callback that will be used to notify interested parties of result
     */
    @Override
    public void insertHabit(@NonNull final Habit habit,
                            @NonNull final HabitsDataSource.SaveHabitCallback saveHabitCallback) {
        checkNotNull(habit);
        try {
            long generatedId =
                    mHabitsLocalDataSource.insertHabit(habit, saveHabitCallback);

            if(mCachedHabits == null) {
                mCachedHabits = new LinkedHashMap<>();
            }

            if(generatedId != SQL_INSERTION_FAIL) {
                habit.setId(generatedId);
                HabitRepetitions habitRepetitions = new HabitRepetitions();
                habitRepetitions.setHabit(habit);
                habitRepetitions.setRepetitions(new HashSet<Repetition>());
                mCachedHabits.put(generatedId, habitRepetitions);
            }
        }
        catch(InterruptedException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        catch (ExecutionException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
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
        if(mCachedHabits != null) {
            loadHabitsCallback.onHabitsLoaded(new ArrayList<>(mCachedHabits.values()));
        }
        else {
            mHabitsLocalDataSource.queryAllHabits(new HabitsDataSource.LoadHabitsCallback() {

                @Override
                public void onHabitsLoaded(@NonNull List<HabitRepetitions> habits) {
                    refreshCache(habits);
                    loadHabitsCallback.onHabitsLoaded(habits);
                }

                @Override
                public void onDataNotAvailable() {
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
        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }
        mCachedHabits.clear();
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
        mHabitsLocalDataSource.insertRepetition(habitId, repetition);
        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }
        mCachedHabits.get(habitId).getRepetitions().add(repetition);
    }

    /**
     * Method deletes repetition to associated habit.
     *
     * @param habitId   the id of habit
     * @param repetition    the repetition that will be removed
     */
    @Override
    public void deleteRepetition(final long habitId, @NonNull final Repetition repetition) {
        mHabitsLocalDataSource.deleteRepetition(habitId, repetition);
        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }
        mCachedHabits.get(habitId).getRepetitions().remove(repetition);
    }
}
