package com.example.waterintake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;
import com.example.waterintake.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private User user;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // ✅ Efficient way to run async tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch user data from the database
        fetchUserFromDatabase(userId);
    }

    private void fetchUserFromDatabase(int userId) {
        executor.execute(() -> {
            user = AppDatabase.getInstance(this).userDao().getUserByIdSync(userId);

            runOnUiThread(() -> {
                if (user != null) {
                    displayUserData(user);
                } else {
                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void displayUserData(User user) {
        binding.tvUserName.setText("Welcome, " + user.getUsername() + "!");

        if ("standard".equals(user.getUnitSystem())) {
            double heightInInches = user.getHeight() / 2.54;
            double weightInLbs = user.getWeight() / 0.453592;

            int feet = (int) (heightInInches / 12);
            int inches = (int) (heightInInches % 12);

            binding.tvUserHeight.setText(String.format("Height: %d' %d\"", feet, inches));
            binding.tvUserWeight.setText(String.format("Weight: %.1f lbs", weightInLbs));
        } else {
            binding.tvUserHeight.setText(String.format("Height: %.1f cm", user.getHeight()));
            binding.tvUserWeight.setText(String.format("Weight: %.1f kg", user.getWeight()));
        }

        binding.tvUserWorkoutLevel.setText("Workout Level: " + user.getWorkoutLevel());

        if (user.getUseCreatine()) {
            binding.creatineLogo.setVisibility(View.VISIBLE);
        } else {
            binding.creatineLogo.setVisibility(View.GONE);
        }

        // ✅ CALCULATE AND DISPLAY WATER GOAL
        double waterGoal = calculateWaterGoal(user);
        binding.tvWaterGoal.setText(String.format("Daily Water Goal: %.1f L", waterGoal));
    }

    // ✅ COPIED & REUSED from UserDashboardActivity
    private double calculateWaterGoal(User user) {
        if (user == null) return 0;

        double waterGoalLiters = user.getWeight() * 0.033;

        if (user.getUseCreatine()) {
            waterGoalLiters *= 1.3;
        }

        switch (user.getWorkoutLevel()) {
            case "High (6–7 times a week)":
                waterGoalLiters *= 1.115;
                break;
            case "Moderate (3–5 times a week)":
                break; // No change
            case "Light (1–3 times a week)":
                waterGoalLiters *= 0.885;
                break;
            case "Sedentary (little/no exercise)":
                waterGoalLiters *= 0.77;
                break;
        }

        return waterGoalLiters;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
