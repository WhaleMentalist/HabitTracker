package us.spencer.habittracker.habitdetails.view;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.spencer.habittracker.BoxedNumber;
import us.spencer.habittracker.R;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;
import us.spencer.habittracker.utility.DateUtils;

public class HabitCalendarAdapter extends RecyclerView.Adapter<HabitCalendarAdapter.DayOfMonthViewHolder> {

    private static final int DEFAULT_YEARS_BACK = 5;

    private List<TimeStamp> mCalendarDays;

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

        for(int i = 0; i < mCalendarDays.size(); i++) {
            mIsComplete.put(i, mRepetitions.contains(new Repetition(mCalendarDays.get(i),
                    mHabit.getId())));
        }
    }

    @Override
    public DayOfMonthViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_habit_calendar, viewGroup, false);
        return new DayOfMonthViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DayOfMonthViewHolder viewHolder, int i) {
        final TimeStamp timeStamp = mCalendarDays.get(i);
        boolean isComplete = mIsComplete.get(i);

        viewHolder.mCalendarDayTextView.setText(DateUtils
                .getDayOfMonthAsString(timeStamp.getDateTime().getDayOfMonth()));

        if(isComplete) {
            viewHolder.mCalendarDayTextView.setBoxColor(Color.parseColor("#FF4081"));
        }
        else {
            viewHolder.mCalendarDayTextView.setBoxColor(Color.parseColor("#4B7B7B7B"));
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mCalendarDays.size();
    }


    public TimeStamp getItemFromAdapterPosition(int position) {
        return mCalendarDays.get(position);
    }

    protected static class DayOfMonthViewHolder extends RecyclerView.ViewHolder {

        private BoxedNumber mCalendarDayTextView;

        private DayOfMonthViewHolder(View itemView) {
            super(itemView);
            mCalendarDayTextView = itemView.findViewById(R.id.day_of_month_tv);
        }
    }
}
