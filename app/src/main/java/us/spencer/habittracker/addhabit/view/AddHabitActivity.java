package us.spencer.habittracker.addhabit.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import us.spencer.habittracker.R;
import us.spencer.habittracker.addhabit.AddHabitPresenter;
import us.spencer.habittracker.utility.Injection;

/**
 * Allow user to add a habit to track
 */
public class AddHabitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        AddHabitFragment addHabitFragment =  (AddHabitFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_habit_frag);
        setTitle(R.string.add_habit_title_act);
        new AddHabitPresenter(Injection.provideHabitsRepository(this), addHabitFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
