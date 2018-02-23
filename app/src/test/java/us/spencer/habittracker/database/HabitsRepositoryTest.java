package us.spencer.habittracker.database;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.ExecutionException;

import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class HabitsRepositoryTest {

    /* Repetitions unique only between days */
    private static long MILLI_IN_DAY = 1000 * 60 * 60 * 24;

    private static final int NUMBER_REPETITIONS = 3;

    private static final long HABIT_ONE_ID = 1;

    private static final long HABIT_TWO_ID = 2;

    private static final long NONEXISTENT_HABIT_ID = 3;

    private static final Habit HABIT_ONE = new Habit(HABIT_ONE_ID, "NAME_ONE", "DESC_ONE");

    private static final Habit HABIT_TWO = new Habit(HABIT_TWO_ID, "NAME_TWO", "DESC_TWO");

    private static final List<HabitRepetitions> MOCK_HABIT_REPETITIONS = Lists.newArrayList(
            new HabitRepetitions(HABIT_ONE),
            new HabitRepetitions(HABIT_TWO)
    );

    private static final List<Repetition> MOCK_REPETITIONS = Lists.newArrayList(
            new Repetition(new TimeStamp(0L), HABIT_ONE_ID),
            new Repetition(new TimeStamp(0L), HABIT_TWO_ID)
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

        /* Need better fix. The DAO seems to be overriding test key due to 'autogenerate'
        * Since we are mocking the database we will simply override it for now. */
        HABIT_ONE.setId(HABIT_ONE_ID);
        HABIT_TWO.setId(HABIT_TWO_ID);

        mHabitsRepository = HabitsRepository.getInstance(mLocalDataSource);
    }

    @After
    public void teardown() {
        clearRepetitions();
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
        verify(mLoadHabitsCallback).onHabitsLoaded(MOCK_HABIT_REPETITIONS);
    }

    @Test
    public void queryAllHabits_forNoSyncCacheRefreshCache() {
        assertThat(mHabitsRepository.isCacheSync, is(false));
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mLocalDataSource).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
        mLoadHabitsCallbackCaptor.getValue().onHabitsLoaded(MOCK_HABIT_REPETITIONS);
        assertThat(mHabitsRepository.isCacheSync, is(true));
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_HABIT_REPETITIONS.size()));
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
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_HABIT_REPETITIONS.size()));
        mHabitsRepository.deleteAllHabits();
        assertThat(mHabitsRepository.isCacheSync, is(true));
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
    }

    @Test
    public void deleteHabitById_deleteFromCacheCorrectly() {
        fillCache();
        mHabitsRepository.deleteHabitById(HABIT_ONE_ID);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_HABIT_REPETITIONS.size() - 1));
        assertThat(mHabitsRepository.mCachedHabits.containsKey(HABIT_ONE_ID), is(false));
        assertThat(mHabitsRepository.mCachedHabits.containsKey(HABIT_TWO_ID), is(true));
    }

    @Test
    public void deleteHabitById_callsDeleteHabitByIdDatabase() {
        fillCache();
        mHabitsRepository.deleteHabitById(HABIT_ONE_ID);
        verify(mLocalDataSource).deleteHabitById(HABIT_ONE_ID);
    }

    @Test
    public void insertRepetition_insertsToCorrectHabit_v1() {
        fillCache();
        mHabitsRepository.insertRepetition(HABIT_ONE_ID, MOCK_REPETITIONS.get(0));
        assertThat(mHabitsRepository.mCachedHabits.get(HABIT_ONE_ID).getRepetitions().size(), is(1));
        assertThat(mHabitsRepository.mCachedHabits.get(HABIT_TWO_ID).getRepetitions().size(), is(0));
    }

    @Test
    public void insertRepetition_insertsToCorrectHabit_v2() {
        fillCache();
        mHabitsRepository.insertRepetition(HABIT_TWO_ID, MOCK_REPETITIONS.get(1));
        assertThat(mHabitsRepository.mCachedHabits.get(HABIT_ONE_ID).getRepetitions().size(), is(0));
        assertThat(mHabitsRepository.mCachedHabits.get(HABIT_TWO_ID).getRepetitions().size(), is(1));
    }

    @Test
    public void insertRepetition_callsInsertRepetitionDatabase_v1() {
        mHabitsRepository.insertRepetition(HABIT_ONE_ID, MOCK_REPETITIONS.get(0));
        verify(mLocalDataSource).insertRepetition(HABIT_ONE_ID, MOCK_REPETITIONS.get(0));
    }

    @Test
    public void insertRepetition_callsInsertRepetitionDatabase_v2() {
        mHabitsRepository.insertRepetition(HABIT_TWO_ID, MOCK_REPETITIONS.get(1));
        verify(mLocalDataSource).insertRepetition(HABIT_TWO_ID, MOCK_REPETITIONS.get(1));
    }

    @Test
    public void deleteRepetition_deletesFromCorrectHabit() {
        fillHabitRepetitions();
        mHabitsRepository.deleteRepetition(MOCK_REPETITIONS.get(0).getHabitId(), MOCK_REPETITIONS.get(0));
        assertThat(mHabitsRepository.mCachedHabits.get(MOCK_REPETITIONS.get(0).getHabitId()).getRepetitions().size(), is(NUMBER_REPETITIONS - 1));
        assertThat(mHabitsRepository.mCachedHabits.get(MOCK_REPETITIONS.get(1).getHabitId()).getRepetitions().size(), is(NUMBER_REPETITIONS));
    }

    @Test
    public void deleteRepetition_callsDeleteRepetitionDatabase() {
        mHabitsRepository.deleteRepetition(MOCK_REPETITIONS.get(0).getHabitId(), MOCK_REPETITIONS.get(0));
        verify(mLocalDataSource).deleteRepetition(MOCK_REPETITIONS.get(0).getHabitId(), MOCK_REPETITIONS.get(0));
    }

    @Test
    public void deleteNonexistentRepetition_noSizeChange() {
        fillHabitRepetitions();
        mHabitsRepository.deleteRepetition(NONEXISTENT_HABIT_ID, MOCK_REPETITIONS.get(0));
        assertThat(mHabitsRepository.mCachedHabits.get(MOCK_REPETITIONS.get(0).getHabitId()).getRepetitions().size(), is(NUMBER_REPETITIONS));
        assertThat(mHabitsRepository.mCachedHabits.get(MOCK_REPETITIONS.get(1).getHabitId()).getRepetitions().size(), is(NUMBER_REPETITIONS));
    }

    @Ignore
    private void fillCache() {
        mHabitsRepository.isCacheSync = true;
        for(HabitRepetitions habitRepetitions : MOCK_HABIT_REPETITIONS) {
            mHabitsRepository.mCachedHabits
                    .put(habitRepetitions.getHabit().getId(), habitRepetitions);
        }
    }

    @Ignore
    private void fillHabitRepetitions() {
        fillCache();
        for(int i = 0; i < NUMBER_REPETITIONS; i++) {
            mHabitsRepository.mCachedHabits.get(HABIT_ONE_ID).getRepetitions().add(new Repetition(new TimeStamp((long)  i * MILLI_IN_DAY), HABIT_ONE_ID));
            mHabitsRepository.mCachedHabits.get(HABIT_TWO_ID).getRepetitions().add(new Repetition(new TimeStamp((long) i * MILLI_IN_DAY), HABIT_TWO_ID));
        }
    }

    @Ignore
    private void clearRepetitions() {
        for(HabitRepetitions habitRepetitions : MOCK_HABIT_REPETITIONS) {
            habitRepetitions.getRepetitions().clear();
        }
    }
}
