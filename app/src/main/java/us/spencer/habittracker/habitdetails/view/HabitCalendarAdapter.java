package us.spencer.habittracker.habitdetails.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Time;
import java.util.List;

import us.spencer.habittracker.R;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;

public class HabitCalendarAdapter extends RecyclerView.Adapter<HabitCalendarAdapter.HabitCalendarViewHolder> {

    private static final int DEFAULT_YEARS_BACK = 1;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("d");

    private List<TimeStamp> mCalendarDays;

    private SparseBooleanArray mSelected;

    private HabitRepetitions mHabitRepetitions;

    public HabitCalendarAdapter(@NonNull HabitRepetitions habitRepetitions) {
        mCalendarDays = TimeStamp.generateDateTimes(DateTime.now()
                .minusYears(DEFAULT_YEARS_BACK), DateTime.now());
        mSelected = new SparseBooleanArray();
        mHabitRepetitions = habitRepetitions;
    }

    @Override
    public HabitCalendarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.habit_calendar_item, viewGroup, false);
        return new HabitCalendarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HabitCalendarViewHolder viewHolder, int i) {
        final TimeStamp timeStamp = mCalendarDays.get(i);
        boolean isComplete = mHabitRepetitions.getRepetitions().contains(
                new Repetition(timeStamp, mHabitRepetitions.getHabit().getId()));

        viewHolder.mCalendarDayTextView.setText(DATE_TIME_FORMATTER.print(timeStamp.getDateTime()));

        if(isComplete) {
            viewHolder.mCalendarDayTextView.setBackgroundResource(R.color.colorAccent);
        }
        else {
            viewHolder.mCalendarDayTextView.setBackgroundResource(R.color.light_gray);
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

    protected class HabitCalendarViewHolder extends RecyclerView.ViewHolder {

        private TextView mCalendarDayTextView;

        private HabitCalendarViewHolder(View itemView) {
            super(itemView);
            mCalendarDayTextView = itemView.findViewById(R.id.calendar_day_tv);
        }
    }
}
