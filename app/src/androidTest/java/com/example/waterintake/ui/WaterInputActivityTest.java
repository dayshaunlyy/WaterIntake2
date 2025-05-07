package com.example.waterintake.ui;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(AndroidJUnit4.class)
public class WaterInputActivityTest {

    private int testUserId;

    @Before
    public void insertTestUser() {
        AppDatabase db = AppDatabase.getInstance(ApplicationProvider.getApplicationContext());

        // Insert user only if doesn't exist
        User testUser = new User("testuser", "testpass");
        db.userDao().insert(testUser);

        // Retrieve inserted user to get ID
        User inserted = db.userDao().getUserByUsername("testuser");
        testUserId = inserted.getId();
    }

    @Test
    public void testLogWaterIntake() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WaterInputActivity.class);
        intent.putExtra("userId", testUserId);

        try (ActivityScenario<WaterInputActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.inputAmount)).check(matches(isDisplayed()));
            onView(withId(R.id.btnLog)).check(matches(isDisplayed()));
            onView(withId(R.id.inputAmount)).perform(typeText("250"));
            onView(withId(R.id.btnLog)).perform(click());
        }
    }
}
