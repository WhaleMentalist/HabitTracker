package us.spencer.habittracker.database;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.spencer.habittracker.model.Habit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation that allows access into database containing
 * habits. For now, we will use local storage. Later, a remote
 * may be implemented for more interesting features
 * (i.e sharing completion of habit or habit list).
 */
public class HabitsRepository implements HabitsDataSource {

    private static HabitsRepository INSTANCE  = null;

    private final HabitsDataSource mHabitsLocalDataSource;

    /** Useful in preventing too many I/O operations that are unnecessary by keeping
     * query results in main memory */
    @VisibleForTesting
    Map<String, Habit> mCachedHabits;

    /**
     * Private constructor to enforce singleton design pattern
     *
     * @param habitsLocalDataSource the local data on device
     */
    private HabitsRepository(@NonNull HabitsDataSource habitsLocalDataSource) {
        mHabitsLocalDataSource = habitsLocalDataSource;
    }

    /**
     * Return single instance of class.
     *
     * @param habitsLocalDataSource the local data on device
     *
     * @return  the {@link HabitsRepository} instance
     */
    public static HabitsRepository getInstance(HabitsDataSource habitsLocalDataSource) {
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
    private void refreshCache(@NonNull List<Habit> habits) {
        checkNotNull(habits);

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        mCachedHabits.clear();

        for(Habit habit : habits) {
            mCachedHabits.put(habit.getName(), habit);
        }
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
     * It will not replace if a duplicate is found
     *
     * @param habit the habit to save in DB
     * @param callback  the callback that will be used to notify interested parties of result
     */
    @Override
    public void saveHabitNoReplace(@NonNull Habit habit, @NonNull SaveHabitCallback callback) {
        checkNotNull(habit);
        mHabitsLocalDataSource.saveHabitNoReplace(habit, callback); /** Save to DB */

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        if(!(mCachedHabits.containsKey(habit.getName()))) {
            mCachedHabits.put(habit.getName(), habit); /** Don't want to replace unless it is absent */
        }
    }

    /**
     * Method saves habit in the database, as well as the 'cache'
     * It will replace if duplicate is found
     *
     * @param habit the habit to save in DB
     * @param callback  the callback that will be used to notify interested parties of result
     */
    @Override
    public void saveHabitReplace(@NonNull Habit habit, @NonNull SaveHabitCallback callback) {
        checkNotNull(habit);
        mHabitsLocalDataSource.saveHabitReplace(habit, callback);

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        mCachedHabits.put(habit.getName(), habit); /** This will replace as intended. */
    }

    /**
     * Method will retrieve all habits from database or cache.
     * Ideally, the cache will contain the information allowing
     * for fast retrieval and less I/O operations.
     *
     * @param callback  the callback that will be used to notify when habits
     *                      are retrieved
     */
    @Override
    public void getHabits(@NonNull final LoadHabitsCallback callback) {
        checkNotNull(callback);

        if(mCachedHabits != null) { /** Check if data is in cache */
            callback.onHabitsLoaded(new ArrayList<>(mCachedHabits.values()));
        }
        else { /** If no data in cache, then load from local storage */
            mHabitsLocalDataSource.getHabits(new LoadHabitsCallback() {

                @Override
                public void onHabitsLoaded(@NonNull List<Habit> habits) {
                    refreshCache(habits); /** Update cache to resulting query */
                    callback.onHabitsLoaded(habits);
                }

                @Override
                public void onDataNotAvailable() {
                    /** TODO: Figure out view layout for no data */
                }

            });
        }
    }

    /**
     * Method will delete all habits from database and the
     * cache.
     */
    @Override
    public void deleteAllHabits() {
        mHabitsLocalDataSource.deleteAllHabits();

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        mCachedHabits.clear();
    }
}
