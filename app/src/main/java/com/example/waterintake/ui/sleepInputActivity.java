package com.example.waterintake.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class sleepInputActivity extends AppCompatActivity {

    private Spinner spinnerWakeHour, spinnerWakePeriod, spinnerSleepHour, spinnerSleepPeriod;
    private TextView tvWakingHours, tvHourlyIntake;
    private final String[] hours = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private final String[] periods = {"AM", "PM"};
    private double totalIntake = 0;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_input);

        // Animate the background of the LinearLayout (hourlyIntakeBox)
        LinearLayout hourlyIntakeBox = findViewById(R.id.hourlyIntakeBox);
        Drawable background = hourlyIntakeBox.getBackground();
        if (background instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) background;
            animationDrawable.setEnterFadeDuration(200);  // Fade-in effect duration
            animationDrawable.setExitFadeDuration(200);   // Fade-out effect duration
            animationDrawable.start();  // Start the animation
        }

        // Animate waterGoalLabel and tvWaterGoal text
        TextView label = findViewById(R.id.hourlyIntakeLabel);
        TextView goal = findViewById(R.id.tvHourlyIntake);

        // Set up alpha animation for labels
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1.0f); // Fade from 30% to 100%
        anim.setDuration(500);  // Duration of the animation
        anim.setRepeatMode(AlphaAnimation.REVERSE);  // Reverse the animation after completion
        anim.setRepeatCount(AlphaAnimation.INFINITE); // Make it repeat indefinitely

        label.startAnimation(anim);
        goal.startAnimation(anim);

        // Initialize spinners and set their listeners
        spinnerWakeHour = findViewById(R.id.spinnerWakeHour);
        spinnerWakePeriod = findViewById(R.id.spinnerWakePeriod);
        spinnerSleepHour = findViewById(R.id.spinnerSleepHour);
        spinnerSleepPeriod = findViewById(R.id.spinnerSleepPeriod);

        tvWakingHours = findViewById(R.id.tvWakingHours);
        tvHourlyIntake = findViewById(R.id.tvHourlyIntake);

        setupSpinners();

        userId = getIntent().getIntExtra("userId", -1);

        if (userId != -1) {
            executor.execute(() -> {
                user = AppDatabase.getInstance(this).userDao().getUserByIdSync(userId);
                if (user != null) {
                    totalIntake = user.getTotalIntake();
                    runOnUiThread(this::calculateAndDisplay);
                    findViewById(R.id.btnSaveContinue).setOnClickListener(v -> {
                        int wakeHour = convertTo24Hour(spinnerWakeHour.getSelectedItem().toString(), spinnerWakePeriod.getSelectedItem().toString());
                        int sleepHour = convertTo24Hour(spinnerSleepHour.getSelectedItem().toString(), spinnerSleepPeriod.getSelectedItem().toString());

                        int sleepDuration;
                        if (sleepHour > wakeHour) {
                            sleepDuration = 24 - (sleepHour - wakeHour);
                        } else {
                            sleepDuration = wakeHour - sleepHour;
                        }

                        int wakingHours = 24 - sleepDuration;
                        double hourlyIntake = (wakingHours > 0) ? totalIntake / wakingHours : 0;

                        user.setWakingHours(wakingHours);
                        user.setHourlyIntake(hourlyIntake);

                        executor.execute(() -> {
                            AppDatabase.getInstance(this).userDao().updateUser(user);  // Persist changes
                            runOnUiThread(() -> {
                                Intent intent = new Intent(sleepInputActivity.this, ProgressActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                finish();
                            }); // Close activity or navigate
                        });
                    });


                    // Back button
                    findViewById(R.id.btnBackToMain).setOnClickListener(v -> {
                        Intent intent = new Intent(sleepInputActivity.this, MainActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                        finish();
                    });

                }
            });
        }
    }

    private void setupSpinners() {
        // Create the adapter for both hours and periods
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hours) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.neonBlue)); // Neon blue color for the selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.neonBlue)); // Neon blue color for dropdown items
                return view;
            }
        };
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> periodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, periods) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.neonBlue)); // Neon blue color for the selected item
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.neonBlue)); // Neon blue color for dropdown items
                return view;
            }
        };
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapters to the spinners
        spinnerWakeHour.setAdapter(hourAdapter);
        spinnerWakePeriod.setAdapter(periodAdapter);
        spinnerSleepHour.setAdapter(hourAdapter);
        spinnerSleepPeriod.setAdapter(periodAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                calculateAndDisplay();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerWakeHour.setOnItemSelectedListener(listener);
        spinnerWakePeriod.setOnItemSelectedListener(listener);
        spinnerSleepHour.setOnItemSelectedListener(listener);
        spinnerSleepPeriod.setOnItemSelectedListener(listener);
    }

    private void calculateAndDisplay() {
        int wakeHour = convertTo24Hour(spinnerWakeHour.getSelectedItem().toString(), spinnerWakePeriod.getSelectedItem().toString());
        int sleepHour = convertTo24Hour(spinnerSleepHour.getSelectedItem().toString(), spinnerSleepPeriod.getSelectedItem().toString());

        int sleepDuration;
        if (sleepHour > wakeHour) {
            sleepDuration = 24 - (sleepHour - wakeHour);
        } else {
            sleepDuration = wakeHour - sleepHour;
        }

        int wakingHours = 24 - sleepDuration;

        tvWakingHours.setText(" " + wakingHours);

        if (wakingHours > 0 && totalIntake > 0) {
            double hourlyIntake = totalIntake / wakingHours;
            tvHourlyIntake.setText(String.format(" %.2f L", hourlyIntake));
        } else {
            tvHourlyIntake.setText(" N/A");
        }
    }

    private int convertTo24Hour(String hourStr, String period) {
        int hour = Integer.parseInt(hourStr);
        if (period.equals("AM")) {
            return (hour == 12) ? 0 : hour;
        } else { // PM
            return (hour == 12) ? 12 : hour + 12;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
