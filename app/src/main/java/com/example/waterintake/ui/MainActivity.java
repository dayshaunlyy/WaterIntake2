package com.example.waterintake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
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
                    binding.btnBack.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
                        intent.putExtra("userId", user.getId());
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void displayUserData(User user) {
        binding.tvUserName.setText(user.getUsername() + " Stats");

        String heightText;
        String weightText;

        if ("standard".equals(user.getUnitSystem())) {
            double heightInInches = user.getHeight() / 2.54;
            double weightInLbs = user.getWeight() / 0.453592;

            int feet = (int) (heightInInches / 12);
            int inches = (int) (heightInInches % 12);

            heightText = String.format("%d' %d\"", feet, inches);
            weightText = String.format("%.1f lbs", weightInLbs);
        } else {
            heightText = String.format("%.1f cm", user.getHeight());
            weightText = String.format("%.1f kg", user.getWeight());
        }

        binding.tvUserHeight.setText(underlineAfterColon("Height", heightText));
        binding.tvUserWeight.setText(underlineAfterColon("Weight", weightText));
        binding.tvUserWorkoutLevel.setText(underlineAfterColon("Workout Level", user.getWorkoutLevel()));

        if (user.getUseCreatine()) {
            binding.creatineLogo.setImageResource(R.mipmap.ic_launcher); // Creatine icon
            binding.creatineYesNo.setText(underlineText("Yes"));
        } else {
            binding.creatineLogo.setImageResource(R.mipmap.ic_logono_c); // No-creatine icon
            binding.creatineYesNo.setText(underlineText("No"));
        }

        binding.creatineLogo.setVisibility(View.VISIBLE);


        double waterGoal = calculateWaterGoal(user);
        binding.tvWaterGoal.setText(underlineText(String.format("%.1f L", waterGoal)));


    }

    private SpannableString underlineText(String text) {// helper method for underlining the text after the needed stat.
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new UnderlineSpan(), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
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
    private SpannableString underlineAfterColon(String label, String value) {
        String fullText = label + ": " + value;
        SpannableString spannable = new SpannableString(fullText);
        int start = fullText.indexOf(":") + 2; // Skip colon and space
        spannable.setSpan(new UnderlineSpan(), start, fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
