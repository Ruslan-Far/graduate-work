package com.ruslan.keyboard;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.inputmethodservice.Keyboard;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class EnEspressoTest {

    @Rule
    public ActivityTestRule<IMESettingsActivity> mActivityRule = new ActivityTestRule<>(IMESettingsActivity.class);

//    @Test
//    public void clickBtnExit() {
//        onView(withId(R.id.exitButton)).perform(click());
//    }

    @Test
    public void printText() {

        onView(withText(R.string.test_activity)).perform(click()).check(matches(isDisplayed()));

        onView(withId(R.id.editText)).perform(click()).check(matches(isDisplayed()));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                onView(withId(R.id.editText)).perform(typeText("ac"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onView(withId(R.id.editText)).perform(typeText("cess "));
            }
            else {
                onView(withId(R.id.editText)).perform(typeText("access "));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void printText() {
////        onView(withId(R.id.editText)).perform(typeText("Привет"));
//        onView(withId(R.id.editText)).perform(click()).check(matches(isDisplayed()));
//    }

//    @Test
//    public void clickItemTest() {
//        onView(withText(R.string.test_activity)).perform(click()).check(matches(isDisplayed()));
//
//        onView(withId(R.id.editText)).perform(click()).check(matches(isDisplayed()));
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        for (int i = 29; i <= 30; i++) {
//            onView(withId(R.id.editText)).perform(pressKey(i));
////            onView(withId(R.id.testEdit)).perform(typeText(i + " code"));
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}