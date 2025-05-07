package com.example.waterintake.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView progressText;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_progress);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(this, UserDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", userId);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_log_water) {
                Intent intent = new Intent(this, WaterInputActivity.class);
                intent.putExtra("userId", userId);
                startActivityForResult(intent, 101);
                return true;
            } else if (id == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            return false;
        });

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User user = db.userDao().getUserByIdSync(userId);

            if (user == null) {
                runOnUiThread(() -> Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show());
                return;
            }

            String today = LocalDate.now().toString(); // format: yyyy-MM-dd
            double todayIntake = db.drinkLogDao().getTodayIntake(userId, today);

            double goal = calculateWaterGoal(user);
            int progressPercent = (int) ((todayIntake / goal) * 100);

            runOnUiThread(() -> {
                progressBar.setProgress(progressPercent);
                progressText.setText("You drank " + (int) todayIntake + "ml of " + (int) goal + "ml (" + progressPercent + "%)");
            });
        });
    }

    private double calculateWaterGoal(User user) {
        double waterGoal = user.getWeight() * 0.033;

        if (user.getUseCreatine()) {
            waterGoal *= 1.3;
        }

        String level = user.getWorkoutLevel();
        switch (level) {
            case "High":
                waterGoal *= 1.115;
                break;
            case "Light":
                waterGoal *= 0.885;
                break;
            case "Sedentary":
                waterGoal *= 0.77;
                break;
            // "Moderate" or unknown = no change
        }

        return waterGoal * 1000; // Convert to ml
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
