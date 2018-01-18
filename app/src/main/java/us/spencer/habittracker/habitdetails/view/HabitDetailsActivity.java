package us.spencer.habittracker.habitdetails.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import us.spencer.habittracker.R;
import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.utility.Injection;

public class HabitDetailsActivity extends AppCompatActivity {

    private static final String HABIT_ID = "HABIT_ID";

    private static final int DEFAULT_HABIT_ID = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);

        HabitsDataSource dataSource = Injection.provideHabitsRepository(this);
        final RecyclerView recyclerView = findViewById(R.id.habit_calendar_rv);

        final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
                8,
                GridLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        HabitRepetitions habit = dataSource
                .getHabitById(getIntent().getLongExtra(HABIT_ID, DEFAULT_HABIT_ID));

        setTitle(habit.getHabit().getName());

        final HabitCalendarAdapter adapter = new HabitCalendarAdapter(habit);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount());
    }
}
