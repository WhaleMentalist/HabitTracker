package us.spencer.habittracker.habitdetails.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import us.spencer.habittracker.R;
import us.spencer.habittracker.habitdetails.HabitDetailsPresenter;
import us.spencer.habittracker.utility.Injection;

public class HabitDetailsActivity extends AppCompatActivity {

    private static final String HABIT_ID = "HABIT_ID";

    private static final String HABIT_NAME = "HABIT_NAME";

    private static final int DEFAULT_HABIT_ID = -1;

    private static final String HABIT_DETAIL_CALENDAR_FRAG = "HABIT_CALENDAR_FRAG";

    private HabitDetailsPresenter mPresenter;

    private FragmentManager mFragmentManager;

    private HabitCalendarFragment mHabitCalendarFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);

        mFragmentManager = getFragmentManager();

        if(savedInstanceState == null) {
            mHabitCalendarFragment = new HabitCalendarFragment();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, mHabitCalendarFragment)
                    .commit();
        }
        else {
            mHabitCalendarFragment = (HabitCalendarFragment)
                    mFragmentManager.getFragment(savedInstanceState, HABIT_DETAIL_CALENDAR_FRAG);
        }

        setTitle(getIntent().getStringExtra(HABIT_NAME));
        mPresenter = new HabitDetailsPresenter(Injection.provideHabitsRepository(this),
                mHabitCalendarFragment, getIntent().getLongExtra(HABIT_ID, DEFAULT_HABIT_ID));
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isFinishing()) {
            mFragmentManager.beginTransaction().remove(mHabitCalendarFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFragmentManager.putFragment(outState, HABIT_DETAIL_CALENDAR_FRAG, mHabitCalendarFragment);
    }
}
