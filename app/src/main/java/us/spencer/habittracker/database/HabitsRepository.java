package us.spencer.habittracker.database;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
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

    private Map<String, Habit> mCachedHabit;

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

        if(mCachedHabit == null) {
            mCachedHabit = new LinkedHashMap<>();
        }

        mCachedHabit.put(habit.getName(), habit);
    }
}
