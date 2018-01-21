package us.spencer.habittracker.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

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
     * Insert a {@link Habit} in the database. If it exists, replace it.
     *
     * @param habit the {@link Habit} to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHabit(@NonNull final Habit habit);

    /**
     * Deletes all {@link Habit} from table
     */
    @Query("DELETE FROM habits")
    void deleteHabits();

    /**
     * Deletes a habit from database with corresponding id.
     * It will also delete {@link us.spencer.habittracker.model.Repetition}
     * objects that have foreign key to {@link Habit}
     *
     * @param habitId   the id of the {@link Habit} to delete
     * @return  the number of rows deleted from teh database.
     *          It helps delimit successful deletion.
     */
    @Query("DELETE FROM habits WHERE id = :habitId")
    int deleteHabitById(final long habitId);
}
