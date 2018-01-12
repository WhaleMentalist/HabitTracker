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
import org.mockito.internal.matchers.Null;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import us.spencer.habittracker.database.local.HabitsLocalDataSource;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertThat;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class HabitsRepositoryTest {

    private static final long HABIT_ONE_ID = 1;

    private static final long HABIT_TWO_ID = 2;

    private static final Habit HABIT_ONE = new Habit("NAME_ONE", "DESC_ONE");

    private static final Habit HABIT_HABIT_ONE_WITH_ID = new Habit(HABIT_ONE_ID, "NAME_ONE",
                                                                    "DESC_ONE");

    private static final Habit HABIT_TWO = new Habit("NAME_TWO", "DESC_TWO");

    private static final Habit HABIT_TWO_WITH_ID = new Habit(HABIT_TWO_ID, "NAME_TWO",
                                                                    "DESC_TWO");

    private static final Habit HABIT_NULL = null;

    private static final List<HabitRepetitions> MOCK_LOCAL_STORAGE = Lists.newArrayList(
            new HabitRepetitions(HABIT_HABIT_ONE_WITH_ID),
            new HabitRepetitions(HABIT_TWO_WITH_ID)
    );

    @Mock
    private HabitsLocalDataSource mHabitsLocalDataSource;

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
        mHabitsRepository = HabitsRepository.getInstance(mHabitsLocalDataSource);
    }

    @After
    public void destroy() {
        HabitsRepository.destroyInstance();
    }

    @Test
    public void insertNewHabitWhenCacheNull_initializeCache() throws InterruptedException,
                                                                        ExecutionException {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits, notNullValue());
    }

    @Test
    public void insertNewHabit_callInsertOnDatabaseCorrectly() throws InterruptedException,
                                                                        ExecutionException {
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        verify(mHabitsLocalDataSource)
                .insertHabit(eq(HABIT_ONE), eq(mSaveHabitCallback));
    }

    @Test
    public void insertNewHabitWhenCacheNull_insertToDatabaseAndSyncCache() throws InterruptedException,
                                                                        ExecutionException {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        when(mHabitsLocalDataSource.
                insertHabit(HABIT_ONE, mSaveHabitCallback)).thenReturn(HABIT_ONE_ID); /** Return id, successful insertion*/
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(1));
        assertThat(mHabitsRepository.mCachedHabits.containsKey(HABIT_ONE_ID), is(true));
    }

    @Test(expected = NullPointerException.class)
    public void insertNullHabit_throwNullPointerException() throws InterruptedException,
                                                                    ExecutionException {
        mHabitsRepository.insertHabit(HABIT_NULL, mSaveHabitCallback);
    }

    @Test(expected = NullPointerException.class)
    public void insertNullHabit_noCallToDataBase() throws InterruptedException,
            ExecutionException {
        mHabitsRepository.insertHabit(HABIT_NULL, mSaveHabitCallback);
        verify(mHabitsLocalDataSource, never()).insertHabit(eq(HABIT_NULL), eq(mSaveHabitCallback));
    }

    @Test(expected = NullPointerException.class)
    public void insertNullHabit_cacheRemainsInSameStateNull() {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        mHabitsRepository.insertHabit(HABIT_NULL, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
    }

    @Test(expected = NullPointerException.class)
    public void insertNullHabit_cacheRemainsSameStateNotNull() {
        fillCache();
        assertThat(mHabitsRepository.mCachedHabits, notNullValue());
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_LOCAL_STORAGE.size()));
        mHabitsRepository.insertHabit(HABIT_NULL, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits, notNullValue());
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_LOCAL_STORAGE.size()));
    }

    @Test(expected = InterruptedException.class)
    public void insertHabit_databaseThrowInterruptedException() throws InterruptedException,
                                                                        ExecutionException {
        doThrow(new FakeInterruptedException("FAKE INTERRUPTED EXCEPTION"))
                .when(mHabitsLocalDataSource).insertHabit(HABIT_ONE, mSaveHabitCallback);
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
    }

    @Test(expected = ExecutionException.class)
    public void insertHabit_databaseThrowExecutionException() throws InterruptedException,
                                                                        ExecutionException {
        doThrow(new FakeExecutionException("FAKE EXECUTION EXCEPTION"))
                .when(mHabitsLocalDataSource).insertHabit(HABIT_ONE, mSaveHabitCallback);
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
    }

    @Test
    public void queryAllHabitsWhenCacheNull_loadFromLocalStorage() {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mHabitsLocalDataSource).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
    }

    @Test
    public void queryAllHabitsWhenCacheNull_syncCache() {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mHabitsLocalDataSource).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
        mLoadHabitsCallbackCaptor.getValue().onHabitsLoaded(MOCK_LOCAL_STORAGE);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_LOCAL_STORAGE.size()));
    }

    @Test
    public void queryAllHabitsWhenCacheInSync_loadFromCache() {
        fillCache();
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_LOCAL_STORAGE.size()));
        mHabitsRepository.queryAllHabits(mLoadHabitsCallback);
        verify(mHabitsLocalDataSource, never()).queryAllHabits(eq(mLoadHabitsCallback));
        verify(mLoadHabitsCallback).onHabitsLoaded(
                new ArrayList<>(mHabitsRepository.mCachedHabits.values()));
    }

    @Test
    public void deleteAllHabits_deleteFromLocalStorageAndCache() {
        fillCache();
        assertThat(mHabitsRepository.mCachedHabits.size(), is(MOCK_LOCAL_STORAGE.size()));
        mHabitsRepository.deleteAllHabits();
        verify(mHabitsLocalDataSource).deleteAllHabits();
    }

    @Test
    public void deleteAllHabitsCacheNull_deleteFromLocalStorageAndInitializeCacheEmpty() {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        mHabitsRepository.deleteAllHabits();
        verify(mHabitsLocalDataSource).deleteAllHabits();
        assertThat(mHabitsRepository.mCachedHabits, notNullValue());
        assertThat(mHabitsRepository.mCachedHabits.size(), is(0));
    }

    /**
     * Helper to fill cache with test data. Imitate that cache
     * was filled from previous database transactions
     */
    @Ignore
    private void fillCache() {
        mHabitsRepository.mCachedHabits = new LinkedHashMap<>();

        for(HabitRepetitions habit : MOCK_LOCAL_STORAGE) {
            mHabitsRepository.mCachedHabits.put(habit.getHabit().getId(), habit);
        }
    }

    private class FakeExecutionException extends ExecutionException {

        private String mMessage = null;

        public FakeExecutionException(String message) {mMessage = message;}

        public String getMessage() {return mMessage;}
    }

    private class FakeInterruptedException extends InterruptedException {

        private String mMessage = null;

        public FakeInterruptedException(String message) {mMessage = message;}

        public String getMessage() {return mMessage;}
    }
}
