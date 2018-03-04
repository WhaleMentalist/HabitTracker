package us.spencer.habittracker.habitdetails.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import us.spencer.habittracker.R;
import us.spencer.habittracker.custom.CustomRecyclerView;
import us.spencer.habittracker.custom.HistoryScrollChart;
import us.spencer.habittracker.habitdetails.HabitCalendarAdapter;
import us.spencer.habittracker.habitdetails.HabitDetailsContract;
import us.spencer.habittracker.model.HabitCalendar;
import us.spencer.habittracker.model.HabitRepetitions;

/**
 * Contains the calendar view showing the history of habit completions
 */
public class HabitDetailsFragment extends Fragment implements HabitDetailsContract.DetailsFragmentView {

    private TextView mHabitDesc;

    private HabitDetailsContract.Presenter mPresenter;

    private HistoryScrollChart mChart;

    public HabitDetailsFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_habit_details,
                container,
                false);
        mChart = root.findViewById(R.id.habit_history_chart);
        mHabitDesc = root.findViewById(R.id.habit_desc_tv);
        Button mAddDays = root.findViewById(R.id.add_days_btn);
        mAddDays.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showAddDaysDialog();
            }
        });
        return root;
    }

    @Override
    public void setPresenter(HabitDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showHabitDetails(HabitRepetitions habitRepetitions) {
        if(isActive()) {
            mHabitDesc.setText(habitRepetitions.getHabit().getDescription()); /* Additional information to display */
            mChart.setHabit(habitRepetitions);
        }
    }

    @Override
    public void showAddDaysDialog() {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


}
