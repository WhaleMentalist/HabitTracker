package us.spencer.habittracker.habitdetails.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import us.spencer.habittracker.R;
import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.utility.Injection;

public class HabitDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);

        HabitsDataSource dataSource = Injection.provideHabitsRepository(this);
        final RecyclerView recyclerView = findViewById(R.id.habit_calendar_rv);

        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
                7,
                GridLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        final HabitCalendarAdapter adapter = new HabitCalendarAdapter(dataSource
                .getHabitById(getIntent().getLongExtra("HABIT_ID", -1)));
        recyclerView.setAdapter(adapter);

        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }
}
