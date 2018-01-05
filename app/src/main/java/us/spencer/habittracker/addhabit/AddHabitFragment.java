package us.spencer.habittracker.addhabit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import us.spencer.habittracker.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains the main UI for adding new habit
 */
public class AddHabitFragment extends Fragment implements AddHabitContract.View {

    private AddHabitContract.Presenter mPresenter;

    private TextView mTitle;

    private TextView mDescription;

    private Spinner mFrequency;

    public AddHabitFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addhabit_frag, container, false);
        mTitle = root.findViewById(R.id.add_habit_title_et);
        mDescription = root.findViewById(R.id.add_habit_desc_et);

        /** Setup adapter for spinner to populate with items*/
        mFrequency = root.findViewById(R.id.add_habit_freq_spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.freq_spinner_values,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFrequency.setAdapter(adapter);

        FloatingActionButton confirmInputAction = root.findViewById(R.id.confirm_input_action);
        confirmInputAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mPresenter.addHabit(mTitle.getText().toString(), mDescription.getText().toString());
            }
        });

        return root;
    }

    @Override
    public void setPresenter(@NonNull AddHabitContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showToast() {
        Toast.makeText(getActivity(), "Added habit successfully!", Toast.LENGTH_SHORT).show();
    }
}
