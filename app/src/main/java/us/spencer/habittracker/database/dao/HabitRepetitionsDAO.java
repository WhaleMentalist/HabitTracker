package us.spencer.habittracker.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import us.spencer.habittracker.model.HabitRepetitions;

@Dao
public interface HabitRepetitionsDAO {

    @Query("SELECT * FROM habits")
    @Transaction
    List<HabitRepetitions> getHabitsWithRepetitions();

    @Query("SELECT * FROM habits WHERE id = :habitId")
    @Transaction
    HabitRepetitions getHabitRepetitions(final long habitId);
}
