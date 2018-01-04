package us.spencer.habittracker.database;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import us.spencer.habittracker.model.Habit;

/**
 * Interface allows contract for database access.
 */
public interface HabitDAO {

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
     * @return  the habit with id
     */
    @Query("SELECT * FROM habits WHERE habit_id = :id")
    Habit getHabitById(Long id);

    /**
     * Insert a habit in the database. If it exists, replace it.
     *
     * @param habit the habit to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHabit(Habit habit);

    /**
     * Delete a habit by id
     *
     * @param id    the habit id
     * @return  the number of habits deleted.
     */
    @Query("DELETE FROM habits WHERE habit_id = :id")
    int deleteHabitById(Long id);

    @Query("DELETE FROM habits")
    void deleteHabits();
}
