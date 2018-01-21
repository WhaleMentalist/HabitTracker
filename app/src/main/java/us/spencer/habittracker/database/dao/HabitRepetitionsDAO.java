package us.spencer.habittracker.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import us.spencer.habittracker.model.HabitRepetitions;

/**
 * Data access object helps to combine {@link us.spencer.habittracker.model.Habit}
 * objects to their respective {@link us.spencer.habittracker.model.Repetition}
 * by using foreign key relations.
 */
@Dao
public interface HabitRepetitionsDAO {

    @Query("SELECT * FROM habits")
    @Transaction
    List<HabitRepetitions> getHabitsWithRepetitions();

    @Query("SELECT * FROM habits WHERE id = :habitId")
    @Transaction
    HabitRepetitions getHabitWithRepetitions(final long habitId);

}
