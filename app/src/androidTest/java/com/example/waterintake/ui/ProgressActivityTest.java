package com.example.waterintake.ui;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.waterintake.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProgressActivityTest {

    @Test
    public void testProgressViewsDisplayed() {
        Intent intent = new Intent();
        intent.putExtra("userId", 1);
        try (ActivityScenario<ProgressActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
            onView(withId(R.id.tvProgressText)).check(matches(isDisplayed()));
        }
    }
}
