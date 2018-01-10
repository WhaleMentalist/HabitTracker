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
public class HabitsRepository implements HabitsDataSource, HabitsDataSource.SyncCacheCallback {

    private static HabitsRepository INSTANCE  = null;

    private final HabitsDataSource.Database mHabitsLocalDataSource;

    /** Useful in preventing too many I/O operations that are unnecessary by keeping
     * query results in main memory */
    @VisibleForTesting
    Map<Long, Habit> mCachedHabits;

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
    private void refreshCache(@NonNull List<Habit> habits) {
        checkNotNull(habits);

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        mCachedHabits.clear();

        for(Habit habit : habits) {
            mCachedHabits.put(habit.getId(), habit);
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
     * It will not replace if a duplicate is found
     *
     * @param habit the habit to save in DB
     * @param callback  the callback that will be used to notify interested parties of result
     */
    @Override
    public void saveHabitNoReplace(@NonNull Habit habit,
                                   @NonNull HabitsDataSource.SaveHabitCallback callback) {
        checkNotNull(habit);
        mHabitsLocalDataSource.insertHabitNoReplace(habit, callback, this);
    }

    /**
     * Method saves habit in the database, as well as the 'cache'
     * It will replace if duplicate is found
     *
     * @param habit the habit to save in DB
     * @param callback  the callback that will be used to notify interested parties of result
     */
    @Override
    public void saveHabitReplace(@NonNull Habit habit,
                                 @NonNull HabitsDataSource.SaveHabitCallback callback) {
        checkNotNull(habit);
        mHabitsLocalDataSource.insertHabitReplace(habit, callback, this);
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
    public void retrieveAllHabits(@NonNull final HabitsDataSource.LoadHabitsCallback callback) {
        checkNotNull(callback);

        if(mCachedHabits != null) { /** Check if data is in cache */
            callback.onHabitsLoaded(new ArrayList<>(mCachedHabits.values()));
        }
        else { /** If no data in cache, then load from local storage */
            mHabitsLocalDataSource.queryAllHabits(new LoadHabitsCallback() {

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
     * Method will delete all habits from database and the cache
     */
    @Override
    public void removeAllHabits() {
        mHabitsLocalDataSource.deleteAllHabits();

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }
        mCachedHabits.clear();
    }

    @Override
    public void onHabitIdGenerated(long id, @NonNull final Habit habit) {
        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }
        habit.setId(id);
        mCachedHabits.put(id, habit);
    }
}
