package us.spencer.habittracker.addhabit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import us.spencer.habittracker.R;
import us.spencer.habittracker.utility.Injection;

/**
 * Allow user to add a habit to track
 */
public class AddHabitActivity extends AppCompatActivity {

    private AddHabitPresenter mAddHabitPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addhabit_act);

        AddHabitFragment addHabitFragment =  (AddHabitFragment) getFragmentManager()
                .findFragmentById(R.id.add_habit_frag);

        mAddHabitPresenter = new AddHabitPresenter(Injection.provideHabitsRepository(this),
                                                        addHabitFragment);
    }
}
