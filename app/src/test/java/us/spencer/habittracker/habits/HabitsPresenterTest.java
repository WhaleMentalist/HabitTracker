package us.spencer.habittracker.habits;

import com.google.common.collect.Lists;

import net.bytebuddy.asm.Advice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import us.spencer.habittracker.database.HabitsDataSource;
import us.spencer.habittracker.model.Habit;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class HabitsPresenterTest {

    private static List<Habit> HABITS;

    @Mock
    private HabitsDataSource mHabitsRepository;

    @Mock
    private HabitsContract.View mHabitsView;

    private HabitsPresenter mPresenter;

    @Captor
    private ArgumentCaptor<HabitsDataSource.LoadHabitsCallback> mLoadHabitsCallbackCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this); /** Initialize mocks */
        when(mHabitsView.isActive()).thenReturn(true); /** Wait for active view */

        HABITS = Lists.newArrayList(new Habit("name1", "desc1"),
                new Habit("name2", "desc2"),
                new Habit("name3", "desc3"));
    }

    @Test
    public void createPresenter_setPresenterToView() {
        mPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        verify(mHabitsView).setPresenter(mPresenter);
    }

    @Test
    public void loadHabits_showAllHabitsInView() {
        mPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mPresenter.loadHabits();

        verify(mHabitsRepository).getHabits(mLoadHabitsCallbackCaptor.capture()); /** Capture callback */
        mLoadHabitsCallbackCaptor.getValue().onHabitsLoaded(HABITS);

        ArgumentCaptor<List> showHabitsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mHabitsView).showHabits(showHabitsArgumentCaptor.capture());
        assertTrue(showHabitsArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void addHabit_callShowAddHabitView() {
        mPresenter = new HabitsPresenter(mHabitsRepository, mHabitsView);
        mPresenter.addHabit();
        verify(mHabitsView).showAddHabit();
    }
}
