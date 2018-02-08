package us.spencer.habittracker.habitdetails.view;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;

import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;
import us.spencer.habittracker.utility.DateUtils;

/**
 * Model that helps to construct and access repetitions and
 * map them to a gridded calendar implementation in the
 * view.
 */
class HabitCalendar {

    /**
     * For now, app will only display data from up to one
     * year ago.
     */
    private static final int DEFAULT_YEARS_BACK = 1;

    /**
     * Delimits the number of days in a column besides
     * the header. Useful for getting number of headers.
     */
    private static final int DAYS_IN_WEEK = 7;

    /**
     * Number of items per column in the grid
     */
    static final int NUMBER_ITEMS_PER_COLUMN = 8;

    /**
     * Holds the calendar days data for the 'RecyclerView'...
     * This will be statically defined and built.
     * For now, the user will only be allowed to see up to a
     * year back, however, older data is stored and counted.
     */
    private static final List<TimeStamp> mCalendar = TimeStamp.generateDateTimes(DateTime.now()
            .minusYears(DEFAULT_YEARS_BACK), DateTime.now());

    /**
     * The {@link Habit} instance associated to the {@link HabitRepetitions}
     */
    private Habit mHabit;

    /**
     * A {@link SparseBooleanArray} that maps an element from
     * mCalendar data member to a boolean that represents if
     * {@link Repetition} falls on that {@link TimeStamp}
     */
    private SparseBooleanArray mIsComplete;

    /**
     * The headers that will be generated from calendar data and
     * mapped to the grid
     */
    private SparseArray<String> mHeaders;

    /**
     * Initialize {@link HabitCalendar} with no associated {@link Habit}
     * to any habit (hence the null assignment) and initializes
     * data
     */
    HabitCalendar() {
        mHabit = null;
        mIsComplete = new SparseBooleanArray();
        mHeaders = new SparseArray<>();
    }

    /**
     * Constructor will initialize and populate the necessary data
     * and header information.
     *
     * @param habitRepetitions the {@link HabitRepetitions} containing the
     *              data necessary to construct mappings to the calendar
     *             that mark the day the repetitions occurred
     */
    HabitCalendar(HabitRepetitions habitRepetitions) {
        long habitId;
        int currentMonth, index = 0;
        int previousMonth = 0;
        Repetition repetition = new Repetition();
        mIsComplete = new SparseBooleanArray();
        mHeaders = new SparseArray<>();

        mHabit = habitRepetitions.getHabit();
        habitId = mHabit.getId();
        repetition.setHabitId(habitId);
        Set<Repetition> repetitions = habitRepetitions.getRepetitions();

        for(TimeStamp timeStamp : mCalendar) { /* Must iterate through each timestamp */
            repetition.setTimeStamp(timeStamp);
            mIsComplete.put(index, repetitions.contains(repetition));
            currentMonth = timeStamp.getDateTime().getMonthOfYear();

            if(index % DAYS_IN_WEEK == 0) { /* Check if new month has started */
                if(currentMonth != previousMonth) {
                    /* Offset by the number of columns in the grid to get correct header position */
                    mHeaders.put(index + (index / DAYS_IN_WEEK),
                            DateUtils.getMonthAsString(currentMonth));
                    previousMonth = currentMonth;
                }
            }
            index++;
        }
    }

    /**
     * Method will retrieve item at specified position and account
     * for the extra header items in the columns using modulus operator
     *
     * @param position  the position in accordance with header items
     *                  accounted
     *
     * @return  the {@link TimeStamp} at the specified position
     */
    TimeStamp getCalendarItemAt(int position) {
        return mCalendar.get(position - (position / NUMBER_ITEMS_PER_COLUMN) - 1);
    }

    /**
     * Retrieve the total number of headers, including the
     * blank ones
     *
     * @return  the number of headers in the model
     */
    private int getNumberHeaders() {
        return (mCalendar.size() / DAYS_IN_WEEK) + 1;
    }

    /**
     * Adds the number of headers along with the number of
     * days in the model
     *
     * @return  total number of items in model
     */
    int getSize() {
        return mCalendar.size() + getNumberHeaders();
    }

    /**
     * Retrieves whether given day that is specified at position
     * is a completed day for the habit. The method will account
     * for the headers in the model.
     *
     * @param position  the position not accounting for the headers
     * @return  the result of whether the day was complete
     */
    boolean isCompleteAt(int position) {
        return mIsComplete.get(position - (position / NUMBER_ITEMS_PER_COLUMN) - 1,
                false);
    }

    /**
     * Method will retrieve the header value at the specified position
     *
     * @param position  the position to retrieve header value
     * @return  the value of header as String
     */
    String getHeaderAt(int position) {
        return mHeaders.get(position, "");
    }
}
