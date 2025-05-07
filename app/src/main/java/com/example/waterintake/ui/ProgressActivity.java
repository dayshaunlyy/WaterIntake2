package com.example.waterintake.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvProgressText, tvProgressPercent;
    private Button btnAddWater, btnReset, btnSettings, btnEditStats;
    private ImageView laserEffectImage; // ImageView for the laser effect

    private double currentIntake = 0;
    private double totalIntake = 0;
    private double hourlyIntake = 0;

    private int userId;
    private User user;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        tvProgressText = findViewById(R.id.tvProgressText);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        btnAddWater = findViewById(R.id.btnAddWater);
        btnReset = findViewById(R.id.btnReset);
        btnSettings = findViewById(R.id.btnSettings);
        btnEditStats = findViewById(R.id.btnEditStats);
        laserEffectImage = findViewById(R.id.laserEffectImage);
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Clear saved preferences (optional)
            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();

            // Navigate to the LoginActivity or your start screen
            Intent intent = new Intent(ProgressActivity.this, LoginActivity.class); // Replace with your actual login activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish();
        });
// ImageView for the laser effect

        // Set the max value for the progress bar
        progressBar.setMax(100);

        // Get user ID passed from the previous activity
        userId = getIntent().getIntExtra("userId", -1);

        // Load user data from database
        if (userId != -1) {
            executor.execute(() -> {
                user = AppDatabase.getInstance(this).userDao().getUserByIdSync(userId);
                if (user != null) {
                    totalIntake = user.getTotalIntake();
                    hourlyIntake = user.getHourlyIntake();
                    currentIntake = user.getCurrentIntake();
                    runOnUiThread(this::updateProgressDisplay);
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show());
                }
            });
        }

        // Add water button
        btnAddWater.setOnClickListener(v -> {
            if (currentIntake + hourlyIntake <= totalIntake) {
                currentIntake += hourlyIntake;
                user.setCurrentIntake(currentIntake); // Save to user object

                executor.execute(() -> {
                    AppDatabase.getInstance(this).userDao().updateUser(user); // Persist it
                });

                updateProgressDisplay();
            }
        });


        // Reset button
        btnReset.setOnClickListener(v -> {
            currentIntake = 0;
            user.setCurrentIntake(currentIntake);

            executor.execute(() -> {
                AppDatabase.getInstance(this).userDao().updateUser(user);
            });

            updateProgressDisplay();
        });



        // Settings button
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        // Edit Stats button
        btnEditStats.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void updateProgressDisplay() {
        // Ensure current intake doesn't exceed total intake
        currentIntake = Math.min(currentIntake, totalIntake);

        // Calculate progress percentage
        int progressPercent = (totalIntake > 0) ? (int) ((currentIntake / totalIntake) * 100) : 0;

        // Animate progress bar
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressPercent);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // Update progress text
        tvProgressText.setText(String.format("You’ve Drank %.2fL of %.2fL (%d%%)", currentIntake, totalIntake, progressPercent));
        tvProgressPercent.setText(progressPercent + "%");

        // Trigger laser effect when progress reaches 100%
        if (progressPercent >= 98) {
            showLaserEffect();
        }
    }

    private void showLaserEffect() {
        // Use a Handler to introduce a delay of 5 seconds before showing the laser effect
        new Handler().postDelayed(() -> {
            // Show the laser effect ImageView
            laserEffectImage.setVisibility(ImageView.VISIBLE);

            // Simple animation for the laser effect (scaling and rotating)
            laserEffectImage.animate()
                    .scaleX(2f)
                    .scaleY(2f)
                    .rotation(360)
                    .setDuration(1000)
                    .withEndAction(() -> {
                        // After the animation ends, reset the laser effect
                        laserEffectImage.setVisibility(ImageView.INVISIBLE);
                        laserEffectImage.setScaleX(1f);
                        laserEffectImage.setScaleY(1f);
                        laserEffectImage.setRotation(0);
                    })
                    .start();
        }, 3000); // Delay for 5000 milliseconds (5 seconds)
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Save the current screen and user ID into SharedPreferences
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .putString("lastScreen", "progress")
                .putInt("lastUserId", userId)
                .apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shut down the executor to avoid memory leaks
        executor.shutdown();
    }
}

