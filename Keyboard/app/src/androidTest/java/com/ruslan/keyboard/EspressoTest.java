package com.ruslan.keyboard;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class EspressoTest {

    @Rule
    public ActivityTestRule<IMESettingsActivity> mActivityRule = new ActivityTestRule<>(IMESettingsActivity.class);

//    @Test
//    public void clickBtnExit() {
//        onView(withId(R.id.exitButton)).perform(click());
//    }

    @Test
    public void clickItemTest() {
        onView(withText(R.string.test_activity)).perform(click()).check(matches(isDisplayed()));

        onView(withId(R.id.editText)).perform(click()).check(matches(isDisplayed()));
//        onView(withId(R.id.editText)).perform(typeText("Hello"));
        onView(withId(R.id.editText)).perform(pressKey(97));
    }

//    @Test
//    public void printText() {
////        onView(withId(R.id.editText)).perform(typeText("Привет"));
//        onView(withId(R.id.editText)).perform(click()).check(matches(isDisplayed()));
//    }
}