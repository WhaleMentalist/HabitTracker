package us.spencer.habittracker.habitdetails;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.spencer.habittracker.custom.BoxedText;
import us.spencer.habittracker.R;
import us.spencer.habittracker.model.HabitCalendar;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.TimeStamp;
import us.spencer.habittracker.utility.DateUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class HabitCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int COMPLETE_DAY_COLOR = Color.parseColor("#FF4081");

    private static final int INCOMPLETE_DAY_COLOR = Color.parseColor("#4B7B7B7B");

    private static final int HEADER_VALUE = 0;

    private static final int ITEM_VALUE = 1;

    private float mTextSize;

    private HabitCalendar mHabitCalendar;

    /**
     * Initialize adapter with blank slate. Basically, a {@link HabitRepetitions} with no completed
     * days and no attachment to a real {@link us.spencer.habittracker.model.Habit}
     *
     * @param textSize  the size of the text in the calendar view
     */
    public HabitCalendarAdapter(float textSize) {
        mTextSize = textSize;
        mHabitCalendar = new HabitCalendar();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType == HEADER_VALUE) { /* Header type */
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.header_habit_calendar, viewGroup, false);
            viewHolder =  new MonthViewHolder(itemView, mTextSize);
        }
        else {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_habit_calendar, viewGroup, false);
            viewHolder =  new DayOfMonthViewHolder(itemView, mTextSize);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof DayOfMonthViewHolder) {
            TimeStamp timeStamp = mHabitCalendar.getCalendarItemAt(i);
            DayOfMonthViewHolder dayOfMonthViewHolder = (DayOfMonthViewHolder) viewHolder;
            boolean isComplete = mHabitCalendar.isCompleteAt(i);

            dayOfMonthViewHolder.mCalendarDay.setText(DateUtils
                    .getDayOfMonthAsString(timeStamp.getDateTime().getDayOfMonth()));

            if(isComplete) {
                dayOfMonthViewHolder.mCalendarDay.setBoxColor(COMPLETE_DAY_COLOR);
            }
            else {
                dayOfMonthViewHolder.mCalendarDay.setBoxColor(INCOMPLETE_DAY_COLOR);
            }
        }
        else {
            MonthViewHolder monthViewHolder = (MonthViewHolder) viewHolder;
            monthViewHolder.mCalendarMonth.setText(mHabitCalendar.getHeaderAt(i));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mHabitCalendar.getSize();
    }

    @Override
    public int getItemViewType(int position) {
        int result;
        if(position % HabitCalendar.NUMBER_ITEMS_PER_COLUMN == 0) { /* At row zero */
            result = HEADER_VALUE;
        }
        else {
            result = ITEM_VALUE; /* At a calendar item */
        }
        return result;
    }

    /**
     * Method will set new data to the calendar object that
     * the adapter will contain
     *
     * @param habit the {@link HabitRepetitions} that will be
     *                  represented and used by adapter for
     *              recycler view.
     */
    public void replaceData(HabitRepetitions habit) {
        setData(habit);
        notifyDataSetChanged();
    }

    /**
     * Assigns new calendar to show on view
     *
     * @param habit the data from {@link HabitRepetitions} to
     *                  show on the calendar view
     */
    private void setData(HabitRepetitions habit) {
        mHabitCalendar = new HabitCalendar(checkNotNull(habit));
    }

    private static class DayOfMonthViewHolder extends RecyclerView.ViewHolder {

        private BoxedText mCalendarDay;

        private DayOfMonthViewHolder(View itemView, float textSize) {
            super(itemView);
            mCalendarDay = itemView.findViewById(R.id.day_of_month_bn);
            mCalendarDay.setTextSize(textSize);
        }
    }

    private static class MonthViewHolder extends RecyclerView.ViewHolder {

        private BoxedText mCalendarMonth;

        private MonthViewHolder(View itemView, float textSize) {
            super(itemView);
            mCalendarMonth = itemView.findViewById(R.id.month_of_year_bn);
            mCalendarMonth.setTextSize(textSize * 0.75f);
        }
    }
}
