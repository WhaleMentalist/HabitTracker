package us.spencer.habittracker.database;

import android.support.annotation.NonNull;

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

    /** Useful in preventing too many I/O operations that are unnecessary */
    private Map<String, Habit> mCachedHabits;

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
     * when called again
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void saveHabit(@NonNull Habit habit, @NonNull SaveHabitCallback callback) {
        checkNotNull(habit);
        mHabitsLocalDataSource.saveHabit(habit, callback); /** Save to DB */

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        mCachedHabits.put(habit.getName(), habit);
    }

    @Override
    public void getHabits(@NonNull final LoadHabitsCallback callback) {
        checkNotNull(callback);

        if(mCachedHabits != null) { /** Check if data is in cache */
            callback.onHabitsLoaded(new ArrayList<>(mCachedHabits.values()));
            return;
        }
        else { /** If no data in cache, then load from local storage */
            mHabitsLocalDataSource.getHabits(new LoadHabitsCallback() {

                @Override
                public void onHabitsLoaded(@NonNull List<Habit> habits) {
                    refreshCache(habits);
                    callback.onHabitsLoaded(habits);
                }

            });
        }
    }

    public void deleteAllHabits() {
        mHabitsLocalDataSource.deleteAllHabits();

        if(mCachedHabits == null) {
            mCachedHabits = new LinkedHashMap<>();
        }

        mCachedHabits.clear();
    }
}
