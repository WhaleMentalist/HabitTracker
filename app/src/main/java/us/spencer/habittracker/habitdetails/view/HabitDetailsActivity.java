package us.spencer.habittracker.habitdetails.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import us.spencer.habittracker.R;
import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.habitdetails.HabitDetailsPresenter;
import us.spencer.habittracker.model.HabitCalendar;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.utility.Injection;

public class HabitDetailsActivity extends AppCompatActivity {

    private static final String HABIT_ID = "HABIT_ID";

    private static final int DEFAULT_HABIT_ID = -1;

    private HabitDetailsPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HabitCalendarFragment habitCalendarFragment = new HabitCalendarFragment();
        fragmentTransaction.add(R.id.fragment_container, habitCalendarFragment);
        fragmentTransaction.commit();

        mPresenter = new HabitDetailsPresenter(Injection.provideHabitsRepository(this),
                habitCalendarFragment, getIntent().getLongExtra(HABIT_ID, DEFAULT_HABIT_ID));
    }
}
