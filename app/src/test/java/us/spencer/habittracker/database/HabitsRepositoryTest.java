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

import java.util.LinkedHashMap;
import java.util.List;

import us.spencer.habittracker.database.local.HabitsLocalDataSource;
import us.spencer.habittracker.model.Habit;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class HabitsRepositoryTest {

    private static List<Habit> DB_HABITS;

    private static final long ID_ONE = 1;

    private static final long ID_TWO = 2;

    private static final String NAME_ONE = "name1";

    private static final String NAME_TWO = "name2";

    private static final String DESC_ONE = "description1";

    private static final String DESC_TWO = "description2";

    private static final Habit VALID_HABIT_ONE = new Habit(NAME_ONE, DESC_ONE);

    private static final Habit VALID_HABIT_TWO = new Habit(NAME_TWO, DESC_TWO);

    private static final Habit DB_HABIT_ONE = new Habit(ID_ONE, NAME_ONE, DESC_ONE);

    private static final Habit DB_HABIT_TWO = new Habit(ID_TWO, NAME_TWO, DESC_TWO);

    @Mock
    private HabitsLocalDataSource mHabitsLocalDataSource;

    @Mock
    private HabitsDataSource.SaveHabitCallback dummySaveHabitCallback;

    @Mock
    private HabitsDataSource.LoadHabitsCallback dummyLoadHabitsCallback;

    @Captor
    private ArgumentCaptor<HabitsDataSource.LoadHabitsCallback> mLoadHabitsCallbackCaptor;

    @Captor
    private ArgumentCaptor<HabitsDataSource.SyncCacheCallback> mSyncCacheCallbackCaptor;

    private HabitsRepository mHabitsRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mHabitsRepository = HabitsRepository.getInstance(mHabitsLocalDataSource);
        DB_HABITS = Lists.newArrayList(DB_HABIT_ONE, DB_HABIT_TWO); /** Two items */
    }

    @After
    public void destroy() {
        HabitsRepository.destroyInstance();
    }

    @Test
    public void saveValidHabit_saveToDatabaseAndCache() {
        assertNull(mHabitsRepository.mCachedHabits);
        mHabitsRepository.saveHabit(VALID_HABIT_ONE, dummySaveHabitCallback);
        verify(mHabitsLocalDataSource).insertHabit(eq(VALID_HABIT_ONE),
                                                    eq(dummySaveHabitCallback),
                                                    mSyncCacheCallbackCaptor.capture());
        mSyncCacheCallbackCaptor.getValue().onHabitIdGenerated(ID_ONE, VALID_HABIT_ONE);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(1));
        assertTrue(mHabitsRepository.mCachedHabits.containsKey(Long.valueOf(ID_ONE)));
    }

    @Test
    public void getHabitsWhenCacheEmpty_loadFromLocalStorage() {
        assertNull(mHabitsRepository.mCachedHabits); /** Precondition: Cache is 'null' */
        mHabitsRepository.retrieveAllHabits(dummyLoadHabitsCallback);
        verify(mHabitsLocalDataSource).queryAllHabits(mLoadHabitsCallbackCaptor.capture());
        mLoadHabitsCallbackCaptor.getValue().onHabitsLoaded(DB_HABITS);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(2)); /** Post-Condition: Cache is not 'null' and is filled with database data */
    }

    @Test
    public void getHabitsWhenCacheGood_loadFromCache() {
        fillCache();
        mHabitsRepository.retrieveAllHabits(dummyLoadHabitsCallback);
        verify(mHabitsLocalDataSource, never()).queryAllHabits(dummyLoadHabitsCallback); /** Should not load from local storage*/
    }

    /**
     * Helper to fill cache with test data. Imitate that cache
     * was filled from previous database transactions
     */
    @Ignore
    private void fillCache() {
        mHabitsRepository.mCachedHabits = new LinkedHashMap<>();

        for(Habit habit : DB_HABITS) {
            mHabitsRepository.mCachedHabits.put(habit.getId(), habit);
        }
    }
}
