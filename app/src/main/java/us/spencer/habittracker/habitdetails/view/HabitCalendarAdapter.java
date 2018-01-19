package us.spencer.habittracker.habitdetails.view;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.spencer.habittracker.custom.CalendarDayView;
import us.spencer.habittracker.R;
import us.spencer.habittracker.model.HabitCalendarModel;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.TimeStamp;
import us.spencer.habittracker.utility.DateUtils;

public class HabitCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int COMPLETE_DAY_COLOR = Color.parseColor("#FF4081");

    private static final int INCOMPLETE_DAY_COLOR = Color.parseColor("#4B7B7B7B");

    private static final int HEADER_VALUE = 0;

    private static final int ITEM_VALUE = 1;

    private static final int NUMBER_ITEMS_PER_COLUMN = 8;

    private HabitCalendarModel mHabitCalendarModel;

    /**
     * TODO: Setup with a {@link android.os.AsyncTask} for better UI performance
     *
     * @param habitRepetitions  {@link HabitRepetitions} that will be used to construct
     *                          the {@link HabitCalendarModel} to more easily map the data
     *                          on the grid
     *
     */
    public HabitCalendarAdapter(@NonNull HabitRepetitions habitRepetitions) {
        mHabitCalendarModel = new HabitCalendarModel(habitRepetitions);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType == HEADER_VALUE) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.header_habit_calendar, viewGroup, false);
            viewHolder =  new MonthViewHolder(itemView);
        }
        else {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_habit_calendar, viewGroup, false);
            viewHolder =  new DayOfMonthViewHolder(itemView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof DayOfMonthViewHolder) {
            TimeStamp timeStamp = mHabitCalendarModel.getCalendarItemAt(i);
            DayOfMonthViewHolder dayOfMonthViewHolder = (DayOfMonthViewHolder) viewHolder;
            boolean isComplete = mHabitCalendarModel.isCompleteAt(i);

            dayOfMonthViewHolder.mCalendarDayTextView.setText(DateUtils
                    .getDayOfMonthAsString(timeStamp.getDateTime().getDayOfMonth()));

            if(isComplete) {
                dayOfMonthViewHolder.mCalendarDayTextView.setBoxColor(COMPLETE_DAY_COLOR);
            }
            else {
                dayOfMonthViewHolder.mCalendarDayTextView.setBoxColor(INCOMPLETE_DAY_COLOR);
            }
        }
        else {
            MonthViewHolder monthViewHolder = (MonthViewHolder) viewHolder;
            monthViewHolder.mCalendarMonthTextView.setText(mHabitCalendarModel.getHeaderAt(i));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mHabitCalendarModel.getSize();
    }

    @Override
    public int getItemViewType(int position) {
        int result;
        if(position % NUMBER_ITEMS_PER_COLUMN == 0) { /* At row zero */
            result = HEADER_VALUE;
        }
        else {
            result = ITEM_VALUE;
        }
        return result;
    }

    protected static class DayOfMonthViewHolder extends RecyclerView.ViewHolder {

        private CalendarDayView mCalendarDayTextView;

        private DayOfMonthViewHolder(View itemView) {
            super(itemView);
            mCalendarDayTextView = itemView.findViewById(R.id.day_of_month_bn);
        }
    }

    protected static class MonthViewHolder extends RecyclerView.ViewHolder {

        private CalendarDayView mCalendarMonthTextView;

        private MonthViewHolder(View itemView) {
            super(itemView);
            mCalendarMonthTextView = itemView.findViewById(R.id.month_of_year_bn);
        }
    }
}
