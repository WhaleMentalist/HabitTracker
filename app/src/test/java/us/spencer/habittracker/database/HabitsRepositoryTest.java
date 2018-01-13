package us.spencer.habittracker.database;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.ExecutionException;

import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HabitsRepositoryTest {

    private static final long HABIT_ONE_ID = 1;

    private static final long HABIT_TWO_ID = 2;

    private static final Habit HABIT_ONE = new Habit(HABIT_ONE_ID, "NAME_ONE", "DESC_ONE");

    private static final Habit HABIT_TWO = new Habit(HABIT_TWO_ID, "NAME_TWO", "DESC_TWO");

    private static final List<HabitRepetitions> MOCK_DATA = Lists.newArrayList(
            new HabitRepetitions(HABIT_ONE),
            new HabitRepetitions(HABIT_TWO)
    );

    @Mock
    private HabitsDataSource mLocalDataSource;

    @Mock
    private HabitsDataSource.SaveHabitCallback mSaveHabitCallback;

    @Mock
    private HabitsDataSource.LoadHabitsCallback mLoadHabitsCallback;

    @Captor
    private ArgumentCaptor<HabitsDataSource.LoadHabitsCallback> mLoadHabitsCallbackCaptor;

    private HabitsRepository mHabitsRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mHabitsRepository = HabitsRepository.getInstance(mLocalDataSource);
    }

    @After
    public void teardown() {
        HabitsRepository.destroyInstance(); /* Way to 'wipe' data for each test */
    }

    @Test
    public void insertHabit_callsInsertLocalDataSource() throws InterruptedException, ExecutionException {
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        verify(mLocalDataSource).insertHabit(HABIT_ONE, mSaveHabitCallback);
    }

    @Test
    public void insertHabit_forValidIdSyncCache() throws InterruptedException, ExecutionException {
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
        when(mLocalDataSource
                .insertHabit(HABIT_ONE, mSaveHabitCallback)).thenReturn(HABIT_ONE_ID);
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(1));
    }

    @Test
    public void insertHabit_forInvalidIdNoSyncCache() throws InterruptedException, ExecutionException {
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
        when(mLocalDataSource
                .insertHabit(HABIT_ONE, mSaveHabitCallback))
                .thenReturn(HabitsRepository.SQL_INSERTION_FAIL);
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
    }

    @Test
    public void insertHabit_returnGeneratedId() throws InterruptedException, ExecutionException {
        when(mLocalDataSource
                .insertHabit(HABIT_ONE, mSaveHabitCallback)).thenReturn(HABIT_ONE_ID);
        assertThat(mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback),
                is(HABIT_ONE_ID));
    }

    @Test
    public void queryAllHabits_forSyncCacheReturnCache() {
        fillCache();
        assertThat(mHabitsRepository.isCacheSync, is(true));
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mLocalDataSource, never()).queryAllHabits
                (any(HabitsDataSource.LoadHabitsCallback.class));
        verify(mLoadHabitsCallback).onHabitsLoaded(MOCK_DATA);
    }

    @Test
    public void queryAllHabits_forNoSyncCacheRefreshCache() {
        assertThat(mHabitsRepository.isCacheSync, is(false));
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mLocalDataSource).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
        mLoadHabitsCallbackCaptor.getValue().onHabitsLoaded(MOCK_DATA);
        assertThat(mHabitsRepository.isCacheSync, is(true));
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_DATA.size()));
    }

    @Test
    public void queryAllHabits_forNoSyncCacheCallLoadHabitsDatabase() {
        assertThat(mHabitsRepository.isCacheSync, is(false));
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mLocalDataSource).queryAllHabits(any(HabitsDataSource.LoadHabitsCallback.class));
    }

    @Test
    public void queryAllHabits_forEmptyDatabaseCallNoDataAvailable() {
        assertThat(mHabitsRepository.isCacheSync, is(false));
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mLocalDataSource).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
        mLoadHabitsCallbackCaptor.getValue().onDataNotAvailable();
        verify(mLoadHabitsCallback).onDataNotAvailable();
    }

    @Test
    public void deleteAllHabits_callsDeleteAllHabitsDatabase() {
        mHabitsRepository.deleteAllHabits();
        verify(mLocalDataSource).deleteAllHabits();
    }

    @Test
    public void deleteAllHabits_callsClearsCache() {
        fillCache();
        assertThat(mHabitsRepository.isCacheSync, is(true));
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_DATA.size()));
        mHabitsRepository.deleteAllHabits();
        assertThat(mHabitsRepository.isCacheSync, is(true));
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
    }


    @Ignore
    private void fillCache() {
        mHabitsRepository.isCacheSync = true;
        for(HabitRepetitions habitRepetitions : MOCK_DATA) {
            mHabitsRepository.mCachedHabits
                    .put(habitRepetitions.getHabit().getId(), habitRepetitions);
        }
    }
}
