package us.spencer.habittracker.habits.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import us.spencer.habittracker.R;
import us.spencer.habittracker.habits.HabitsPresenter;
import us.spencer.habittracker.utility.Injection;

/**
 * Displays the list of habits being tracked
 */
public class HabitsActivity extends AppCompatActivity {

    private HabitsPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits_list);
        HabitsFragment habitsFragment = (HabitsFragment) getFragmentManager()
                .findFragmentById(R.id.habits_frag);
        mPresenter = new HabitsPresenter(Injection.provideHabitsRepository(this),
                                            habitsFragment);
    }
}
