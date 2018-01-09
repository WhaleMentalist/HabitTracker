package us.spencer.habittracker.habits.habits;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import us.spencer.habittracker.R;
import us.spencer.habittracker.database.HabitsRepository;
import us.spencer.habittracker.habits.HabitsActivity;
import us.spencer.habittracker.utility.Injection;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.core.AllOf.allOf;

/** TODO: Change custom {@link Matcher} to match a whole item in the recycler view, rather than just containing a single piece of text*/

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HabitsScreenTest {

    private static final String HABIT_NAME_ONE = "NAME1";

    private static final String HABIT_DESC_ONE = "DESC1";

    private static final String HABIT_NAME_TWO = "NAME2";

    private static final String HABIT_DESC_TWO = "DESC2";

    /**
     * This will be performed before the activity is launched for
     * each test.
     */
    @Rule
    public ActivityTestRule<HabitsActivity> mHabitsActivityTestRule =
            new ActivityTestRule<HabitsActivity>(HabitsActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    HabitsRepository repository =
                            Injection.provideHabitsRepository(InstrumentationRegistry.getTargetContext());
                    repository.deleteAllHabits();
                }
            };

    /**
     * Custom {@link Matcher} which matches an item in a {@link RecyclerView}
     * by its text value
     *
     * @param titleText the title text to match
     * @return  {@link Matcher} that matches the text with the view item
     */
    private Matcher<View> withTitleText(final String titleText) {
        checkArgument(!TextUtils.isEmpty(titleText),
                "titleText cannot be 'null' or empty");

        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View item) {
                return allOf(isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(titleText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA 'RecyclerView' with text " + titleText);
            }
        };
    }

    /**
     * Method will ensure that when habit is added that it is displayed
     * in {@link RecyclerView} with correct data.
     *
     * @throws Exception
     */
    @Test
    public void addHabitToHabitsList() throws Exception {
        createHabit(HABIT_NAME_ONE, HABIT_DESC_ONE);
        onView(withTitleText(HABIT_NAME_ONE)).check(matches(isDisplayed()));
    }

    /**
     * Method will ensure that when a duplicate habit is added by user that
     * it is updated and displayed in {@link RecyclerView} with correct data.
     *
     * @throws Exception
     */
    @Test
    public void addDuplicateHabitToHabitsList() throws Exception {
        createHabit(HABIT_NAME_ONE, HABIT_DESC_ONE);
        createHabit(HABIT_NAME_ONE, HABIT_DESC_TWO);
        onView(withText(R.string.duplicate_title)).check(matches(isDisplayed())); /** Way to check if dialog window opened*/
        onView(withText(R.string.duplicate_dialog_positive_btn)).perform(click()); /** Since dialog is not saved in a layout file, when need to use android default to get dialog button */
        onView(withTitleText(HABIT_NAME_ONE)).check(matches(isDisplayed())); /** Check if name is the same */
        onView(withTitleText(HABIT_DESC_TWO)).check(matches(isDisplayed())); /** Check if description changed */
    }

    /**
     * Method will ensure that when a duplicate habit addition is canceled by user
     * in dialog that the {@link RecyclerView} displays the previous habit information.
     *
     * @throws Exception
     */
    @Test
    public void cancelAddDuplicateHabitToHabitsList() throws Exception {
        createHabit(HABIT_NAME_ONE, HABIT_DESC_ONE);
        createHabit(HABIT_NAME_ONE, HABIT_DESC_TWO);
        onView(withText(R.string.duplicate_title)).check(matches(isDisplayed())); /** Way to check if dialog window opened*/
        onView(withText(R.string.duplicate_dialog_negative_btn)).perform(click()); /** Since dialog is not saved in a layout file, when need to use android default to get dialog button */
        Espresso.pressBack(); /** Navigate back to list */
        onView(withTitleText(HABIT_NAME_ONE)).check(matches(isDisplayed())); /** Check if name is the same */
        onView(withTitleText(HABIT_DESC_ONE)).check(matches(isDisplayed())); /** Check if description is same */
    }

    /**
     * Method will ensure that when two unique habits are added that they
     * are both displayed with correct information.
     *
     * @throws Exception
     */
    @Test
    public void addTwoHabitsToHabitsList() throws Exception {
        createHabit(HABIT_NAME_ONE, HABIT_DESC_ONE);
        createHabit(HABIT_NAME_TWO, HABIT_DESC_TWO);
        onView(withTitleText(HABIT_NAME_ONE)).check(matches(isDisplayed()));
        onView(withTitleText(HABIT_DESC_ONE)).check(matches(isDisplayed()));
        onView(withTitleText(HABIT_NAME_TWO)).check(matches(isDisplayed()));
        onView(withTitleText(HABIT_DESC_TWO)).check(matches(isDisplayed()));
    }

    /**
     * Utility method that gestures to add a habit and fill fields with
     * specified values.
     *
     * @param title the title of habit
     * @param desc  the description of habit
     */
    private void createHabit(final String title, final String desc) {
        onView(withId(R.id.action_add)).perform(click()); /** Gestures to add */

        /** Keyboard input gesture */
        onView(withId(R.id.add_habit_title_et)).perform(typeText(title),
                closeSoftKeyboard());
        onView(withId(R.id.add_habit_desc_et)).perform(typeText(desc),
                closeSoftKeyboard());

        /** Confirm and add habit gesture */
        onView(withId(R.id.confirm_input_action)).perform(click());
    }

}
