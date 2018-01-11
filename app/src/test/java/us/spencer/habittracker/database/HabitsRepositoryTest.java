package us.spencer.habittracker.database;

import com.google.common.collect.Lists;

import org.joda.time.Instant;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import us.spencer.habittracker.database.local.HabitsLocalDataSource;
import us.spencer.habittracker.model.Habit;
import us.spencer.habittracker.model.HabitRepetitions;
import us.spencer.habittracker.model.Repetition;
import us.spencer.habittracker.model.TimeStamp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertThat;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class HabitsRepositoryTest {

    private static final long HABIT_ONE_ID = 1;

    private static final Habit HABIT_ONE = new Habit("NAME_ONE", "DESC_ONE");

    private static final Habit HABIT_HABIT_ONE_WITH_ID = new Habit(HABIT_ONE_ID, "NAME_ONE",
                                                                    "DESC_ONE");

    private static final Habit HABIT_NULL = null;

    private static final List<HabitRepetitions> MOCK_LOCAL_STORAGE = Lists.newArrayList(
            new HabitRepetitions(HABIT_HABIT_ONE_WITH_ID)
    );

    @Mock
    private HabitsLocalDataSource mHabitsLocalDataSource;

    @Mock
    private HabitsDataSource.SaveHabitCallback mSaveHabitCallback;

    @Mock
    private HabitsDataSource.LoadHabitsCallback mLoadHabitsCallback;

    @Captor
    private ArgumentCaptor<HabitsDataSource.LoadHabitsCallback> mLoadHabitsCallbackCaptor;

    @Captor
    private ArgumentCaptor<HabitsDataSource.SyncCacheCallback> mSyncCacheCallbackCaptor;

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
    public void insertNewHabit_insertToDatabaseAndSyncCache() {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        mHabitsRepository.insertHabit(HABIT_ONE, mSaveHabitCallback);
        verify(mHabitsLocalDataSource).insertHabit(eq(HABIT_ONE), eq(mSaveHabitCallback),
                mSyncCacheCallbackCaptor.capture());
        mSyncCacheCallbackCaptor.getValue().onHabitIdGenerated(HABIT_ONE_ID, HABIT_ONE);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(1));
        assertThat(mHabitsRepository.mCachedHabits.containsKey(HABIT_ONE_ID), is(true));
    }

    @Test(expected = NullPointerException.class)
    public void insertNullHabit_throwNullPointerException() {
        assertThat(mHabitsRepository.mCachedHabits, nullValue());
        mHabitsRepository.insertHabit(HABIT_NULL, mSaveHabitCallback);
        verify(mHabitsLocalDataSource, never())
                .insertHabit(HABIT_NULL, mSaveHabitCallback, mHabitsRepository);
    }

    @Test
    public void queryAllHabitsWhenCacheNull_loadFromLocalStorage() {
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
}
