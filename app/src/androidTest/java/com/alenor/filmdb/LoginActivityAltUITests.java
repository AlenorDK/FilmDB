package com.alenor.filmdb;

import android.support.annotation.IdRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.ui.LoginActivityAlt;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class LoginActivityAltUITests {
    @Rule
    public ActivityTestRule<LoginActivityAlt> activityTestRule = new ActivityTestRule<LoginActivityAlt>(LoginActivityAlt.class);

    @Test
    public void incorrectUsernameMessage() {
        onView(withId(R.id.login_activity_alt_username_et)).perform(typeText("asd12"), closeSoftKeyboard());
        onView(withId(R.id.login_activity_alt_login_button)).perform(click());
        onView(withId(R.id.login_activity_alt_login_information)).check(matches(withText("Username must only contain latin letters and can not be empty")));
    }

    @Test
    public void emptyUsernameMessage() {
        onView(withId(R.id.login_activity_alt_username_et)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.login_activity_alt_login_button)).perform(click());
        onView(withId(R.id.login_activity_alt_login_information)).check(matches(withText("Username must only contain latin letters and can not be empty")));
    }

    @Test
    public void shortPasswordMessage() {
        onView(withId(R.id.login_activity_alt_username_et)).perform(typeText("asd"), closeSoftKeyboard());
        onView(withId(R.id.login_activity_alt_password_et)).perform(typeText("dsa"), closeSoftKeyboard());
        onView(withId(R.id.login_activity_alt_login_button)).perform(click());
        onView(withId(R.id.login_activity_alt_login_information)).check(matches(withText("Password must contain at least 6 symbols")));
    }

    @Test
    public void testGuestSession() {
        onView(withId(R.id.login_activity_alt_guest_label)).perform(click());
    }
}
