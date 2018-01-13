package us.spencer.habittracker.habits;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.database.HabitsRepository;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HabitsPresenterTest {

    private static final long HABIT_ID = 1;

    private static final TimeStamp TIME_STAMP = new TimeStamp(Long.valueOf(1000L));

    private static final List<HabitRepetitions> MOCK_HABITS_LIST = Lists.newArrayList(
            new HabitRepetitions(new Habit("NAME_ONE", "DESC_ONE")),
            new HabitRepetitions(new Habit("NAME_TWO", "DESC_TWO"))
    );

    @Mock
    private HabitsRepository mHabitsRepository;

    @Mock
    private HabitsContract.View mHabitsView;

    @Captor
    private ArgumentCaptor<HabitsDataSource.LoadHabitsCallback> mLoadHabitsCallbackCaptor;

    private HabitsPresenter mHabitsPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mHabitsView.isActive()).thenReturn(true);
    }

    @Test
    public void onStart_callLoadHabits() {
        mHabitsPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mHabitsPresenter.start();
        verify(mHabitsPresenter).loadHabits();
    }

    @Test
    public void onLoadHabits_callShowHabitsList() {
        mHabitsPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mHabitsPresenter.loadHabits();
        verify(mHabitsRepository).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
        mLoadHabitsCallbackCaptor.getValue().onHabitsLoaded(MOCK_HABITS_LIST);
        verify(mHabitsView).showHabits(MOCK_HABITS_LIST);
    }

    @Test
    public void onDataNotAvailable_callShowEmptyHabits() {
        mHabitsPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mHabitsPresenter.loadHabits();
        verify(mHabitsRepository).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
        mLoadHabitsCallbackCaptor.getValue().onDataNotAvailable();
        verify(mHabitsView).showEmptyHabits();
    }

    @Test
    public void addHabit_callShowAddHabit() {
        mHabitsPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mHabitsPresenter.addHabit();
        verify(mHabitsView).showAddHabit();
    }

    @Test
    public void addRepetition_callsInsertRepetition() {
        mHabitsPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mHabitsPresenter.addRepetition(HABIT_ID, TIME_STAMP);
        Repetition repetition = new Repetition(TIME_STAMP, HABIT_ID);
        verify(mHabitsRepository).insertRepetition(eq(HABIT_ID), eq(repetition));
    }

    @Test
    public void deleteRepetition_callsDeleteRepetition() {
        mHabitsPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mHabitsPresenter.deleteRepetition(HABIT_ID, TIME_STAMP);
        Repetition repetition = new Repetition(TIME_STAMP, HABIT_ID);
        verify(mHabitsRepository).deleteRepetition(eq(HABIT_ID), eq(repetition));
    }
}
