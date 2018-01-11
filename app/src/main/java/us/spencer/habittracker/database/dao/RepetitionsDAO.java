package us.spencer.habittracker.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;

import us.spencer.habittracker.model.Repetition;

@Dao
public interface RepetitionsDAO {

    /**
     * Insert a {@link Repetition} in the database. If it exists, replace it.
     *
     * @param repetition the {@link Repetition} to be inserted
     */
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void insertRepetition(@NonNull final Repetition repetition);

    /**
     * Delete a {@link Repetition} in the database.
     *
     * @param repetition    the {@link Repetition} to be deleted
     */
    @Delete
    void deleteRepetition(final Repetition repetition);

}
