package com.ruslan.keyboard;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.os.Binder;
import android.os.IBinder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.ServiceTestRule;

import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class ServEspressoTest {

    @Rule
    public ActivityTestRule<IMESettingsActivity> mActivityRule = new ActivityTestRule<>(IMESettingsActivity.class);

    @Rule
    public final ServiceTestRule mServiceTestRule = new ServiceTestRule();

    @Test
    public void clickItemTest() throws TimeoutException {
        onView(withText(R.string.test_activity)).perform(click()).check(matches(isDisplayed()));

        onView(withId(R.id.editText)).perform(click()).check(matches(isDisplayed()));
//        onView(withId(R.id.editText)).perform(typeText("Hello"));
//        onView(withId(R.id.editText)).perform(pressKey(97));

//        Intent serviceIntent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), IME.class);
        Intent serviceIntent = new Intent(ApplicationProvider.getApplicationContext(), IME.class);

//        mServiceTestRule.unbindService();

        IBinder binder = mServiceTestRule.bindService(serviceIntent);

//        IME.LocalBinder localBinder = (IME.LocalBinder) binder;

//        IME ime = ((IME.LocalBinder) binder).getService();

//        ime.onKey(Keyboard.KEYCODE_SHIFT, null);

//        IME ime1 =
    }

    @Test
    public void testWithBoundService() throws TimeoutException {

        onView(withText(R.string.test_activity)).perform(click()).check(matches(isDisplayed()));

        onView(withId(R.id.editText)).perform(click()).check(matches(isDisplayed()));

        // Create the service Intent.
//        Intent serviceIntent =
//                new Intent(ApplicationProvider.getApplicationContext(), LocalService.class);

        // Data can be passed to the service via the Intent.
//        serviceIntent.putExtra(LocalService.SEED_KEY, 42L);

        // Bind the service and grab a reference to the binder.
//        IBinder binder = mServiceTestRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call public methods on the binder directly.
//        LocalService service = ((LocalService.LocalBinder) binder).getService();

        // Verify that the service is working correctly.
//        assertThat(service.getRandomInt(), is(any(Integer.class)));
    }
}