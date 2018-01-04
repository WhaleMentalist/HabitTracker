package us.spencer.habittracker.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import us.spencer.habittracker.model.Habit;

/**
 * Singleton class that constructs instance of database.
 */
@Database(entities = {Habit.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "basic-habit-db";

    private static AppDatabase INSTANCE;

    public abstract HabitDAO habitDAO();

    private static final Object sLock = new Object();

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
