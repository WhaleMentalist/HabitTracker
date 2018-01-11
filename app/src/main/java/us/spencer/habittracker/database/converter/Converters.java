package us.spencer.habittracker.database.converter;

import android.arch.persistence.room.TypeConverter;

import org.joda.time.Chronology;
import org.joda.time.chrono.ISOChronology;

import us.spencer.habittracker.model.TimeStamp;

public class Converters {

    @TypeConverter
    public static TimeStamp fromTimeStamp(Long value) {
        return value == null ? null : new TimeStamp(value);
    }

    @TypeConverter
    public static Long timeStampToMillis(TimeStamp timeStamp) {
        return timeStamp == null ? null : timeStamp.getDateTime().getMillis();
    }
}
