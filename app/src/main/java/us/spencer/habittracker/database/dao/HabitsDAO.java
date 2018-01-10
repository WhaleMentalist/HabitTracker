package us.spencer.habittracker.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import us.spencer.habittracker.model.Habit;

/**
 * Interface allows contract for database access.
 */
@Dao
public interface HabitsDAO {

    /** Select all habits from table.
     *
     * @return all habits
     */
    @Query("SELECT * FROM habits")
    List<Habit> getHabits();

    /**
     * Select habit by id.
     *
     * @param id    the habit id
     * @return  the {@link Habit} with id
     */
    @Query("SELECT * FROM habits WHERE id = :id")
    Habit getHabitById(long id);

    /**
     * Insert a {@link Habit} in the database. If it exists, replace it.
     *
     * @param habit the {@link Habit} to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHabit(Habit habit);

    /**
     * Delete a {@link Habit} by name
     *
     * @param id    the habit id
     * @return  the number of habits deleted
     */
    @Query("DELETE FROM habits WHERE id = :id")
    int deleteHabitById(long id);

    /**
     * Deletes all {@link Habit} from table
     */
    @Query("DELETE FROM habits")
    void deleteHabits();
}
