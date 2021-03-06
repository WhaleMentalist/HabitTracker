package us.spencer.habittracker.habits.view;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import us.spencer.habittracker.R;
import us.spencer.habittracker.addhabit.view.AddHabitActivity;
import us.spencer.habittracker.habitdetails.view.HabitDetailsActivity;
import us.spencer.habittracker.habits.HabitsContract;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays the list of habits user is tracking.
 *
 * TODO: Edit feature needed!
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
        View root = inflater.inflate(R.layout.fragment_habits_list, container, false);
        setHasOptionsMenu(true);

        RecyclerView rv = root.findViewById(R.id.habits_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);

        mAdapter = new HabitsAdapter(new ArrayList<HabitRepetitions>());
        rv.setAdapter(mAdapter);
        registerForContextMenu(rv); /* Allow use of context menu on recycler view items */
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
     */
    @Override
    public void showAddHabit() {
        Intent intent = new Intent(getActivity(), AddHabitActivity.class);
        startActivity(intent);
    }

    @Override
    public void showEmptyHabits() {
        /* TODO: Implement view to show empty habits */
    }

    @Override
    public void showHabitDetails(final Habit habit) {
        Intent intent = new Intent(getActivity(), HabitDetailsActivity.class);
        intent.putExtra("HABIT_ID", habit.getId());
        intent.putExtra("HABIT_NAME", habit.getName());
        startActivity(intent);
    }

    @Override
    public void showHabits(List<HabitRepetitions> habits) {
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

        private List<HabitRepetitions> mHabits;

        private HabitsAdapter(List<HabitRepetitions> habits) {
            setList(habits);
        }

        /**
         * Method will reassign list of habits to contain.
         *
         * @param habits    the new list to assign
         */
        void replaceData(List<HabitRepetitions> habits) {
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
        private void setList(List<HabitRepetitions> habits) {
            mHabits = checkNotNull(habits);
        }

        @Override
        public HabitViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_habit, viewGroup, false);

            return new HabitViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final HabitViewHolder viewHolder, int i) {
            final HabitRepetitions habit = mHabits.get(i);
            viewHolder.mHabitName.setText(habit.getHabit().getName());
            viewHolder.mHabitDesc.setText(habit.getHabit().getDescription());
            boolean isComplete = habit.getRepetitions()
                    .contains(new Repetition(TimeStamp.getToday(), habit.getHabit().getId()));

            if(isComplete) {
                viewHolder.mHabitStatus.setChecked(true);
                viewHolder.mHabitStatus.setCheckMarkDrawable(R.drawable.ic_check_complete);
            }
            else {
                viewHolder.mHabitStatus.setChecked(false);
                viewHolder.mHabitStatus.setCheckMarkDrawable(R.drawable.ic_check_incomplete);
            }

            viewHolder.mPopupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popup = new PopupMenu(view.getContext(), viewHolder.mPopupMenu);

                    /* Inflate the menu form resource file*/
                    popup.inflate(R.menu.habits_list_context_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case R.id.delete_item:
                                    /* Get item position in adapter to save copy of pending deletion item */
                                    final int pos = viewHolder.getAdapterPosition();
                                    final long habitId = mHabits.get(pos).getHabit().getId();
                                    final HabitRepetitions copy = mHabits.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, mHabits.size());

                                    /* Show snackbar that allows user to undo deletion before actual database deletion occurs */
                                    Snackbar snackbar = Snackbar.make(view, copy.getHabit().getName() + " removed", Snackbar.LENGTH_LONG);
                                    snackbar.setActionTextColor(getResources().getColor(R.color.magenta));
                                    snackbar.addCallback(new Snackbar.Callback() { /* Allows detection of dismissal*/
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            /* Want timeout dismissal or consecutive event , not user dismissal */
                                            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT ||
                                                    event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                                                mPresenter.deleteHabitById(habitId); /* Purge from local storage */
                                            }
                                        }
                                    });
                                    /* Undo action for deletion */
                                    snackbar.setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mAdapter.restoreHabit(copy, pos);
                                        }
                                    });
                                    snackbar.show();

                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
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
         * Restores item that is up for pending deletion.
         *
         * @param habit the {@link HabitRepetitions} that is pending for deletion
         * @param pos   the previous position of the item before deletion occurred
         */
        private void restoreHabit(HabitRepetitions habit, int pos) {
            mHabits.add(pos, habit);
            notifyItemInserted(pos);
        }

        /**
         * {@link HabitViewHolder} allows reduced calls to 'getView', which
         * increase allowable scroll speed. Allows application to 'hold' view
         * item in memory instead of creating a new view, which is costly.
         */
        class HabitViewHolder extends RecyclerView.ViewHolder {

            private TextView mHabitName;

            private TextView mHabitDesc;

            private TextView mPopupMenu;

            private CheckedTextView mHabitStatus;

            private HabitViewHolder(View itemView) {
                super(itemView);
                registerForContextMenu(itemView);
                mHabitName = itemView.findViewById(R.id.habit_item_name_tv);
                mHabitDesc = itemView.findViewById(R.id.habit_item_desc_tv);
                mPopupMenu = itemView.findViewById(R.id.popup_menu_tv);

                mHabitStatus = itemView.findViewById(R.id.habit_item_status_ctv);
                mHabitStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Vibrator vibrator = (Vibrator)
                                getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                        final Habit habit = mHabits.get(getAdapterPosition()).getHabit();
                        CheckedTextView checkedTextView = ((CheckedTextView) view);

                        if(checkedTextView.isChecked()) {
                            checkedTextView.setChecked(false);
                            checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_incomplete);
                            mPresenter.deleteRepetition(habit.getId(),
                                    new TimeStamp(Instant.now()));
                            /* May not be a vibrator on phone*/
                            if (vibrator != null) {
                                vibrator.vibrate(30);
                            }
                        }
                        else {
                            checkedTextView.setChecked(true);
                            checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_complete);
                            mPresenter.addRepetition(habit.getId(),
                                    new TimeStamp(Instant.now()));
                            /* May not be a vibrator on phone*/
                            if (vibrator != null) {
                                vibrator.vibrate(30);
                            }
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mPresenter.loadHabitDetails(mHabits.get(getAdapterPosition()).getHabit());
                    }
                });
            }
        }
    }
}
