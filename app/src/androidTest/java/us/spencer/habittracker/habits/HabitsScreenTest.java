package us.spencer.habittracker.habits;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import us.spencer.habittracker.R;
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

public class HabitsScreenTest {

    private static final String HABIT_NAME_ONE = "NAME1";

    private static final String HABIT_DESC_ONE = "DESC1";

    private static final String HABIT_NAME_TWO = "NAME2";

    @Rule
    public ActivityTestRule<HabitsActivity> mHabitsActivityTestRule =
            new ActivityTestRule<HabitsActivity>(HabitsActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    Injection.provideHabitsRepository(InstrumentationRegistry.getTargetContext());
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

    @Test
    public void addHabitToHabitsList() throws Exception {
        createHabit(HABIT_NAME_ONE, HABIT_DESC_ONE);

        onView(withTitleText(HABIT_NAME_ONE)).check(matches(isDisplayed()));
    }

    /**
     * Utility method that gestures to add a habit and fill fields with
     * specified values
     *
     * @param title the title of habit
     * @param desc  the description of habit
     */
    private void createHabit(final String title, final String desc) {
        onView(withId(R.id.action_add)).perform(click()); /** Gestures to add */

        onView(withId(R.id.add_habit_title_et)).perform(typeText(title),
                closeSoftKeyboard());
        onView(withId(R.id.add_habit_desc_et)).perform(typeText(desc),
                closeSoftKeyboard());

        onView(withId(R.id.confirm_input_action)).perform(click());
    }

}
