package us.spencer.habittracker.habitdetails.view;

import android.graphics.Color;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.spencer.habittracker.custom.CalendarDayView;
import us.spencer.habittracker.R;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Month;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;
import us.spencer.habittracker.utility.DateUtils;

public class HabitCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int DEFAULT_YEARS_BACK = 1;

    private static final int COMPLETE_DAY_COLOR = Color.parseColor("#FF4081");

    private static final int INCOMPLETE_DAY_COLOR = Color.parseColor("#4B7B7B7B");

    private static final int HEADER_VALUE = 0;

    private static final int ITEM_VALUE = 1;

    private static final int NUMBER_OF_ROW = 8;

    private int prevMonth = -1;

    private List<TimeStamp> mCalendarDays;

    private List<String> mHeaders;

    private Habit mHabit;

    private Set<Repetition> mRepetitions;

    private Map<Integer, Boolean> mIsComplete;

    /**
     * TODO: Setup with a {@link android.os.AsyncTask} for better UI performance
     *
     * @param habitRepetitions
     */
    public HabitCalendarAdapter(@NonNull HabitRepetitions habitRepetitions) {
        mCalendarDays = TimeStamp.generateDateTimes(DateTime.now()
                .minusYears(DEFAULT_YEARS_BACK), DateTime.now());
        mHabit = habitRepetitions.getHabit();
        mRepetitions = habitRepetitions.getRepetitions();
        mIsComplete = new HashMap<>();
        mHeaders = new ArrayList<>();
        int currMonth;

        for(int i = 0; i < mCalendarDays.size(); i++) {
            mIsComplete.put(i, mRepetitions.contains(new Repetition(mCalendarDays.get(i),
                    mHabit.getId())));
            currMonth = mCalendarDays.get(i).getDateTime().getMonthOfYear();

            if(i % 7 == 0) { /* Check header, does it need month or blank */
                if(prevMonth != currMonth) {
                    mHeaders.add(DateUtils.getMonthAsString(currMonth));
                    prevMonth = currMonth;
                }
                else {
                    mHeaders.add("");
                }
            }
        }
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
            int newPos = getDataPosition(i);
            TimeStamp timeStamp = mCalendarDays.get(newPos);
            DayOfMonthViewHolder dayOfMonthViewHolder = (DayOfMonthViewHolder) viewHolder;
            boolean isComplete = mIsComplete.get(newPos);

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
            monthViewHolder.mCalendarMonthTextView.setText(mHeaders.get(i / 8));
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        int size = mCalendarDays.size();
        return size + (size / (NUMBER_OF_ROW - 1)) + 1; /* Must add additional headers */
    }

    @Override
    public int getItemViewType(int position) {
        int result;
        if(position % NUMBER_OF_ROW == 0) { /* At row zero */
            result = HEADER_VALUE;
        }
        else {
            result = ITEM_VALUE;
        }
        return result;
    }

    public int getDataPosition(int position) {
        int numberCol = position / NUMBER_OF_ROW;
        return position - numberCol - 1;
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
