package us.spencer.habittracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import us.spencer.habittracker.database.converter.Converters;
import us.spencer.habittracker.database.dao.HabitsDAO;
import us.spencer.habittracker.database.dao.RepetitionsDAO;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.Repetition;


/**
 * Singleton class that constructs instance of database.
 */
@Database(entities = {Habit.class, Repetition.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "HabitTracker.db";

    private static AppDatabase INSTANCE;

    public abstract HabitsDAO habitDAO();

    public abstract RepetitionsDAO repetitionsDAO();

    private static final Object sLock = new Object(); /** Need access to DB to be managed fairly and without anomalies */

    public static AppDatabase getInstance(final Context context) {
        synchronized (sLock) {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, DB_NAME).build();
            }
        }
        return INSTANCE;
    }
}
