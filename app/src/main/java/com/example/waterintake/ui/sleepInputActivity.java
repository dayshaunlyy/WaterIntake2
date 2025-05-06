package com.example.waterintake.ui;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
                }
            });
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
