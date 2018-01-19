package us.spencer.habittracker.habitdetails.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import us.spencer.habittracker.R;
import us.spencer.habittracker.habitdetails.HabitDetailsContract;
import us.spencer.habittracker.model.HabitRepetitions;

public class HabitCalendarFragment extends Fragment implements HabitDetailsContract.CalendarFragmentView {

    private HabitDetailsContract.Presenter mPresenter;

    private HabitCalendarAdapter mAdapter;

    public HabitCalendarFragment() {}

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
        View root = inflater.inflate(R.layout.fragment_habit_details_calendar,
                container,
                false);
        RecyclerView rv = root.findViewById(R.id.habit_calendar_rv);
        rv.setLayoutManager(new GridLayoutManager(getActivity(),
                HabitCalendar.NUMBER_ITEMS_PER_COLUMN,
                GridLayout.HORIZONTAL, false));
        rv.setHasFixedSize(true);
        mAdapter = new HabitCalendarAdapter();
        rv.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void setPresenter(HabitDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    public void showHistory(HabitRepetitions habitRepetitions) {
        if(isActive()) {
            mAdapter.replaceData(habitRepetitions);
        }
    }
}
