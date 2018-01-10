package us.spencer.habittracker.habits;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import us.spencer.habittracker.R;
import us.spencer.habittracker.addhabit.AddHabitActivity;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays the list of habits user is tracking.
 *
 * Very useful article explaining 'RecyclerView':
 * https://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class HabitsFragment extends Fragment implements HabitsContract.View {

    private HabitsContract.Presenter mPresenter;

    private HabitsAdapter mAdapter;

    public HabitsFragment() {}

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.habits_frag, container, false);
        setHasOptionsMenu(true);

        RecyclerView rv = root.findViewById(R.id.habits_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);

        mAdapter = new HabitsAdapter(new ArrayList<Habit>());
        rv.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if(itemID == R.id.action_add) {
            mPresenter.addHabit();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches activity that pertains to adding a new habit
     *
     * TODO: Add intent argument to signify type of action
     */
    @Override
    public void showAddHabit() {
        Intent intent = new Intent(getActivity(), AddHabitActivity.class);
        startActivity(intent);
    }

    @Override
    public void showHabits(List<Habit> habits) {
        mAdapter.replaceData(habits);
    }

    @Override
    public void setPresenter(@Nonnull HabitsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     * Adapter to allow recycler view to display habits in a list. Adapter
     * allows recycler view to make efficient use of memory and lower amount of
     * calls to find UI elements.
     */
    private class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.HabitViewHolder> {

        private List<Habit> mHabits;

        public HabitsAdapter(List<Habit> habits) {
            setList(habits);
        }

        /**
         * Method will reassign list of habits to contain.
         *
         * @param habits    the new list to assign
         */
        public void replaceData(List<Habit> habits) {
            setList(habits);
            notifyDataSetChanged();
        }

        /**
         * Method will assign new list of habits.
         * It will perform a 'null' check on the passed
         * list.
         *
         * @param habits    the new list to assign
         */
        private void setList(List<Habit> habits) {
            mHabits = checkNotNull(habits);
        }

        @Override
        public HabitViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.habit_item, viewGroup, false);
            HabitViewHolder habitViewHolder = new HabitViewHolder(itemView);
            return habitViewHolder;
        }

        @Override
        public void onBindViewHolder(final HabitViewHolder viewHolder, int i) {
            final Habit habit = mHabits.get(i);
            viewHolder.mHabitName.setText(habit.getName());
            viewHolder.mHabitDesc.setText(habit.getDescription());
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public int getItemCount() {
            return mHabits.size();
        }

        /**
         * {@link HabitViewHolder} allows reduced calls to 'getView', which
         * increase allowable scroll speed. Allows application to 'hold' view
         * item in memory instead of creating a new view.
         */
        public class HabitViewHolder extends RecyclerView.ViewHolder {

            private TextView mHabitName;

            private TextView mHabitDesc;

            private Switch mHabitStatus;

            public HabitViewHolder(View itemView) {
                super(itemView);
                mHabitName = itemView.findViewById(R.id.habit_item_name_tv);
                mHabitDesc = itemView.findViewById(R.id.habit_item_desc_tv);
                mHabitStatus = itemView.findViewById(R.id.habit_status_switch);
                mHabitStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                        final Habit habit = mHabits.get(getAdapterPosition());
                        // Repetition repetition = new Repetition(new TimeStamp(Instant.now()),
                        //         habit.getId());
                        mPresenter.addRepetition(habit.getId(), new TimeStamp(Instant.now()));
                        // mPresenter.addRepetition(new TimeStamp(Instant.now()), habit.getId());

                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        final Habit habit = mHabits.get(getAdapterPosition());
                        Toast.makeText(getActivity(),
                                habit.getId() + " : " + habit.getName() + " was clicked",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
