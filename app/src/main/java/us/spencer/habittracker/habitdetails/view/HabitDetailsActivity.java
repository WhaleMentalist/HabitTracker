package us.spencer.habittracker.habitdetails.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;

import us.spencer.habittracker.R;
import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.TimeStamp;
import us.spencer.habittracker.utility.Injection;

public class HabitDetailsActivity extends AppCompatActivity {

    private View lastCenter;

    private TextView monthTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);

        monthTextView = findViewById(R.id.month_txt);
        HabitsDataSource dataSource = Injection.provideHabitsRepository(this);
        RecyclerView recyclerView = findViewById(R.id.habit_calendar_rv);

        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
                7,
                GridLayoutManager.HORIZONTAL,
                false);

        recyclerView
                .setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        final HabitCalendarAdapter adapter = new HabitCalendarAdapter(dataSource
                .getHabitById(getIntent()
                        .getLongExtra("HABIT_ID", -1)));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(lastCenter != null) {
                    lastCenter.findViewById(R.id.calendar_day_tv)
                            .setBackgroundResource(R.color.light_gray);
                }

                lastCenter = layoutManager.getChildAt(layoutManager.getChildCount() / 2);
                int position = recyclerView.getChildAdapterPosition(lastCenter);
                TimeStamp timeStamp= adapter.getItemFromAdapterPosition(position);

                if(lastCenter != null) {
                    lastCenter.findViewById(R.id.calendar_day_tv)
                            .setBackgroundResource(R.color.magenta);
                    monthTextView.setText(timeStamp.getDateTime()
                            .toString(DateTimeFormat.forPattern("MMM")));
                }
            }
        });
    }
}
