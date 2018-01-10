package us.spencer.habittracker.addhabit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AddHabitPresenterTest {

    private static final Habit validHabit = new Habit("name", "description");

    private static final Habit invalidHabit = new Habit("", "");

    @Mock
    private HabitsDataSource mHabitsRepository;

    @Mock
    private AddHabitContract.View mAddHabitsView;

    private AddHabitPresenter mPresenter;

    @Captor
    private ArgumentCaptor<HabitsDataSource.SaveHabitCallback> mSaveHabitCallback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mAddHabitsView.isActive()).thenReturn(true);
    }

    @Test
    public void createPresenter_setPresenterToView() {
        mPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitsView);
        verify(mAddHabitsView).setPresenter(mPresenter);
    }

    @Test
    public void saveValidHabit_callRepo() {
        mPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitsView);
        mPresenter.addHabit(validHabit.getName(), validHabit.getDescription());
        verify(mHabitsRepository).saveHabit(validHabit, mPresenter);
    }

    @Test
    public void saveValidHabit_callShowHabitsList() {
        mPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitsView);
        mPresenter.addHabit(validHabit.getName(), validHabit.getDescription());
        verify(mHabitsRepository).saveHabit(eq(validHabit), mSaveHabitCallback.capture());
        mSaveHabitCallback.getValue().onHabitSaved();
        verify(mAddHabitsView).showHabitsList();
    }

    @Test
    public void saveInvalidHabit_noCallRepo() {
        mPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitsView);
        mPresenter.addHabit(invalidHabit.getName(), invalidHabit.getDescription());
        verify(mHabitsRepository, never()).saveHabit(invalidHabit, mPresenter);
    }

    @Test
    public void saveInvalidHabit_callShowEmptyHabitError() {
        mPresenter = new AddHabitPresenter(mHabitsRepository, mAddHabitsView);
        mPresenter.addHabit(invalidHabit.getName(), invalidHabit.getDescription());
        verify(mAddHabitsView).showEmptyHabitError();
    }

}
