package us.spencer.habittracker.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

/**
 * The habit the user wants to track
 */
@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId = 0;

    @ColumnInfo(name = "habit_name")
    @NonNull
    private String mName;

    @ColumnInfo(name = "habit_desc")
    @NonNull
    private String mDescription;

    public long getId() {
        return mId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    public void setId(long id) {
        mId = id;
    }

    public void setName(@NonNull String name) {
        mName = name;
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    /**
     * Check if habit contains no data
     *
     * @return whether habit has no useful data
     */
    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mName) ||
                Strings.isNullOrEmpty(mDescription);
    }
}
