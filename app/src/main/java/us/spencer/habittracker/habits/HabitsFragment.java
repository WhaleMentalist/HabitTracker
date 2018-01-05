package us.spencer.habittracker.habits;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import javax.annotation.Nonnull;

import us.spencer.habittracker.R;
import us.spencer.habittracker.model.Habit;

import static com.google.common.base.Preconditions.checkNotNull;

public class HabitsFragment extends Fragment implements HabitsContract.View {

    private HabitsContract.Presenter mPresenter;

    public HabitsFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.habits_frag, container, false);
        return root;
    }

    @Override
    public void showHabits(List<Habit> habits) {

    }

    @Override
    public void setPresenter(@Nonnull HabitsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
