package us.spencer.habittracker.habits;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import us.spencer.habittracker.R;

/**
 * Displays the list of habits being tracked
 */
public class HabitsActivity extends AppCompatActivity {

    private HabitsPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habits_act);

        HabitsFragment habitsFragment = (HabitsFragment) getFragmentManager()
                .findFragmentById(R.id.habits_frag);
    }
}
