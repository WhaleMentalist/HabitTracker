package us.spencer.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import us.spencer.habittracker.addhabit.AddHabitActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.add_habit_action);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createHabit();
            }

        });
    }

    private void createHabit() {
        Intent addHabitIntent = new Intent(this, AddHabitActivity.class);
        startActivity(addHabitIntent);
    }

}
