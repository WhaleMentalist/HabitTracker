package us.spencer.habittracker.addhabit.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import us.spencer.habittracker.R;
import us.spencer.habittracker.addhabit.AddHabitContract;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains the main UI for adding new habit
 */
public class AddHabitFragment extends Fragment implements AddHabitContract.View {

    private AddHabitContract.Presenter mPresenter;

    private TextView mTitle;

    private TextView mDescription;

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
        View root = inflater.inflate(R.layout.fragment_add_habit, container, false);
        mTitle = root.findViewById(R.id.add_habit_name_et);
        mDescription = root.findViewById(R.id.add_habit_desc_et);

        Spinner frequency = root.findViewById(R.id.add_habit_frequency_spn);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.freq_spinner_values,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(adapter);

        FloatingActionButton confirmInputAction = root.findViewById(R.id.add_habit_confirm_fab);
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
    public void showHabitsList() {
        getActivity().finish();
    }

    @Override
    public void showEmptyHabitError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.empty_field_error_title)
                .setMessage(R.string.empty_field_error_mess)
                .setCancelable(false)
                .setPositiveButton(R.string.empty_error_dialog_positive_btn,
                        new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void showAddHabitError() {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

}
