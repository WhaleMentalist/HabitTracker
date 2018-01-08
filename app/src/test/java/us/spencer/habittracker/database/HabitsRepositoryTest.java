package us.spencer.habittracker.database;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class HabitsRepositoryTest {

    private static List<Habit> HABITS;

    private static final String NAME_ONE = "name1";

    private static final String NAME_TWO = "name2";

    private static final String DESC_ONE = "description1";

    private static final String DESC_TWO = "description2";

    private static final Habit VALID_HABIT_ONE = new Habit(NAME_ONE, DESC_ONE);

    private static final Habit VALID_HABIT_TWO = new Habit(NAME_TWO, DESC_TWO);

    private static final Habit DUPLICATE_HABIT_ONE = new Habit(NAME_ONE, DESC_TWO);

    @Mock
    private HabitsLocalDataSource mHabitsLocalDataSource;

    @Mock
    private HabitsDataSource.SaveHabitCallback dummySaveHabitCallback;

    @Mock
    private HabitsDataSource.LoadHabitsCallback dummyLoadHabitsCallback;

    @Captor
    private ArgumentCaptor<HabitsDataSource.LoadHabitsCallback> mCaptor;

    private HabitsRepository mHabitsRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mHabitsRepository = HabitsRepository.getInstance(mHabitsLocalDataSource);

        HABITS = Lists.newArrayList(VALID_HABIT_ONE, VALID_HABIT_TWO); /** Two items */
    }

    @After
    public void destroy() {
        HabitsRepository.destroyInstance();
    }

    @Test
    public void saveValidHabitNoReplace_saveToDatabaseAndCache() {
        mHabitsRepository.saveHabitNoReplace(VALID_HABIT_ONE, dummySaveHabitCallback);
        verify(mHabitsLocalDataSource).saveHabitNoReplace(VALID_HABIT_ONE, dummySaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(1));
    }

    @Test
    public void saveDuplicateHabitNoReplace_doesNotSaveCache() {
        mHabitsRepository.saveHabitNoReplace(VALID_HABIT_ONE, dummySaveHabitCallback);
        mHabitsRepository.saveHabitNoReplace(DUPLICATE_HABIT_ONE, dummySaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(1));
        assertThat(mHabitsRepository.mCachedHabits.get(NAME_ONE).getDescription(), is(DESC_ONE));
    }

    @Test
    public void saveDuplicateHabitReplace_saveToDatabaseAndCache() {
        mHabitsRepository.saveHabitNoReplace(VALID_HABIT_ONE, dummySaveHabitCallback);
        mHabitsRepository.saveHabitReplace(DUPLICATE_HABIT_ONE, dummySaveHabitCallback);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(1));
        assertThat(mHabitsRepository.mCachedHabits.get(NAME_ONE).getDescription(), is(DESC_TWO));
    }

    @Test
    public void getHabitsWhenCacheEmpty_loadFromLocalStorage() {
        assertNull(mHabitsRepository.mCachedHabits); /** Precondition: Cache is 'null' */
        mHabitsRepository.getHabits(dummyLoadHabitsCallback);
        verify(mHabitsLocalDataSource).getHabits(mCaptor.capture());
        mCaptor.getValue().onHabitsLoaded(HABITS);
        assertThat(mHabitsRepository.mCachedHabits.size(), is(2)); /** Post-Condition: Cache is not 'null' and is filled with database data */
    }

    @Test
    public void getHabitsWhenCacheGood_loadFromCache() {
        fillCache();
        mHabitsRepository.getHabits(dummyLoadHabitsCallback);
        verify(mHabitsLocalDataSource, never()).getHabits(dummyLoadHabitsCallback); /** Should not load from local storage*/
    }

    /**
     * Helper to fill cache with test data. Imitate that cache
     * was filled from previous database transactions
     */
    private void fillCache() {
        mHabitsRepository.mCachedHabits = new LinkedHashMap<>();

        for(Habit habit : HABITS) {
            mHabitsRepository.mCachedHabits.put(habit.getName(), habit);
        }
    }
}
