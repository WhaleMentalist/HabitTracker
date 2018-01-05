package us.spencer.habittracker.database;

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
     * @param name    the habit name
     * @return  the habit with name
     */
    @Query("SELECT * FROM habits WHERE habit_name = :name")
    Habit getHabitById(String name);

    /**
     * Insert a habit in the database. If it exists, replace it.
     *
     * @param habit the habit to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHabit(Habit habit);

    /**
     * Delete a habit by name
     *
     * @param name    the habit name
     * @return  the number of habits deleted.
     */
    @Query("DELETE FROM habits WHERE habit_name = :name")
    int deleteHabitById(String name);

    /**
     * Deletes all habits from table
     */
    @Query("DELETE FROM habits")
    void deleteHabits();
}
