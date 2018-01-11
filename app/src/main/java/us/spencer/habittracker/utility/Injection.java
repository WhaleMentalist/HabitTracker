package us.spencer.habittracker.utility;

import android.content.Context;
import android.support.annotation.NonNull;

import us.spencer.habittracker.database.AppDatabase;
import us.spencer.habittracker.database.HabitsRepository;
import us.spencer.habittracker.database.local.HabitsLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility that creates necessary dependencies for an object.
 */
public class Injection {

    public static HabitsRepository provideHabitsRepository(@NonNull Context context) {
        checkNotNull(context);
        AppDatabase database = AppDatabase.getInstance(context);
        return HabitsRepository.getInstance(HabitsLocalDataSource.getInstance(new AppExecutors(),
                database.habitDAO(), database.repetitionsDAO(), database.habitRepetitionsDAO()));
    }
}
