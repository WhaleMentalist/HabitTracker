package us.spencer.habittracker.addhabit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.database.HabitsRepository;
import us.spencer.habittracker.model.Habit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AddHabitPresenterTest {

    private static final String HABIT_NAME = "NAME_ONE";

    private static final String HABIT_DESC = "DESC_ONE";

    @Mock
    private HabitsRepository mHabitsRepository;

    @Mock
    private AddHabitContract.View mAddHabitView;

    @Captor
    private ArgumentCaptor<HabitsDataSource.SaveHabitCallback> mSaveHabitCallbackCaptor;

    private AddHabitPresenter mAddHabitPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mAddHabitView.isActive()).thenReturn(true); /* Just want test to process quickly*/
    }

    @Test
    public void onConstruction_assignPresenterToView() {
        mAddHabitPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitView);
        verify(mAddHabitView).setPresenter(mAddHabitPresenter);
    }

    @Test
    public void addValidHabit_callInsertHabitCorrectly() throws InterruptedException,
                                                                ExecutionException {
        mAddHabitPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitView);
        mAddHabitPresenter.addHabit(HABIT_NAME, HABIT_DESC);
        verify(mHabitsRepository)
                .insertHabit(new Habit(HABIT_NAME, HABIT_DESC), mAddHabitPresenter);
    }

    @Test
    public void addEmptyHabit_callShowEmptyHabitError() throws InterruptedException,
                                                                ExecutionException {
        mAddHabitPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitView);
        mAddHabitPresenter.addHabit("", "");
        verify(mAddHabitView).showEmptyHabitError();
        verify(mHabitsRepository, never())
                .insertHabit(new Habit("", ""), mAddHabitPresenter);
    }

    @Test
    public void onHabitSaved_callShowHabitsList() throws InterruptedException,
                                                                ExecutionException{
        mAddHabitPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitView);
        mAddHabitPresenter.addHabit(HABIT_NAME, HABIT_DESC);
        verify(mHabitsRepository)
                .insertHabit(eq(new Habit(HABIT_NAME, HABIT_DESC)), mSaveHabitCallbackCaptor.capture());
        mSaveHabitCallbackCaptor.getValue().onHabitSaved();
        verify(mAddHabitView).showHabitsList();
    }

    @Test
    public void addValidHabit_onInterruptedShowAddHabitError() throws InterruptedException,
                                                                    ExecutionException {
        mAddHabitPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitView);
        when(mHabitsRepository
                .insertHabit(eq(new Habit(HABIT_NAME, HABIT_DESC)), eq(mAddHabitPresenter)))
                .thenThrow(InterruptedException.class);
        mAddHabitPresenter.addHabit(HABIT_NAME, HABIT_DESC);
        verify(mAddHabitView).showAddHabitError();
    }

    @Test
    public void addValidHabit_onExecutionShowAddHabitError() throws InterruptedException,
            ExecutionException {
        mAddHabitPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitView);
        when(mHabitsRepository
                .insertHabit(eq(new Habit(HABIT_NAME, HABIT_DESC)), eq(mAddHabitPresenter)))
                .thenThrow(ExecutionException.class);
        mAddHabitPresenter.addHabit(HABIT_NAME, HABIT_DESC);
        verify(mAddHabitView).showAddHabitError();
    }
}
