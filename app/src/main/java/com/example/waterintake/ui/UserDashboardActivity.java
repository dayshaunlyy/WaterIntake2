package com.example.waterintake.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.AdapterView;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;
import com.example.waterintake.databinding.ActivityUserDashboardBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDashboardActivity extends AppCompatActivity {

    private ActivityUserDashboardBinding binding;
    private User user;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // ✅ More efficient
    private Switch yourSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }

        yourSwitch = findViewById(R.id.transferSwitch);
        setupSwitch();

        loadUserDetails(userId);

        // ✅ Set up Workout Level Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.workout_levels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.workoutLevelSpinner.setAdapter(adapter);

        binding.btnEditProfile.setOnClickListener(v -> saveChanges());
        binding.btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        setupSwitch();
        setupCreatineSwitch();
        setupWorkoutSpinner();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_progress) {
                Toast.makeText(this, "Progress clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                prefs.edit().clear().apply();

                Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            return false;
        });


    }

    private void setupWorkoutSpinner() {
        binding.workoutLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedWorkoutLevel = parent.getItemAtPosition(position).toString();

                // ✅ Change background color based on selection
                switch (selectedWorkoutLevel) {
                    case "High":
                        binding.workoutLevelSpinner.setBackgroundColor(ContextCompat.getColor(UserDashboardActivity.this, android.R.color.holo_red_light));
                        break;
                    case "Moderate":
                        binding.workoutLevelSpinner.setBackgroundColor(ContextCompat.getColor(UserDashboardActivity.this, android.R.color.holo_orange_light));
                        break;
                    case "Light":
                        binding.workoutLevelSpinner.setBackgroundColor(ContextCompat.getColor(UserDashboardActivity.this, android.R.color.holo_green_light));
                        break;
                    case "Sedentary":
                        binding.workoutLevelSpinner.setBackgroundColor(ContextCompat.getColor(UserDashboardActivity.this, android.R.color.holo_blue_light));
                        break;
                    default:
                        binding.workoutLevelSpinner.setBackgroundColor(ContextCompat.getColor(UserDashboardActivity.this, android.R.color.darker_gray));
                        break;
                }

                // Optional: Toast showing selected
                Toast.makeText(UserDashboardActivity.this, "Workout Level: " + selectedWorkoutLevel, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupSwitch() {
        yourSwitch.setOnCheckedChangeListener((buttonView, isCheckedVal) -> {
            if (user != null) {
                if (isCheckedVal) {
                    user.setUnitSystem("metric");
                } else {
                    user.setUnitSystem("standard");
                }
            }
            setupViewByUnit();
            prefillHeightWeight();
        });
    }


    private void setupCreatineSwitch() {
        binding.creatineSwitch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (user != null) {
                user.setUseCreatine(isChecked);
            }
        });
    }

    private void setupViewByUnit() {
        if (user == null) return;

        if ("standard".equals(user.getUnitSystem())) {
            binding.etUserHeightCm.setVisibility(View.GONE);
            binding.standardHeightLayout.setVisibility(View.VISIBLE);
            binding.etUserWeight.setHint("Weight (lbs)");
        } else {
            binding.etUserHeightCm.setVisibility(View.VISIBLE);
            binding.standardHeightLayout.setVisibility(View.GONE);
            binding.etUserHeightCm.setHint("Height (cm)");
            binding.etUserWeight.setHint("Weight (kg)");
        }
    }

    private void loadUserDetails(int userId) {
        executor.execute(() -> {
            user = AppDatabase.getInstance(this).userDao().getUserByIdSync(userId);

            runOnUiThread(() -> {
                if (user != null) {
                    binding.tvUserName.setText("Welcome, " + user.getUsername() + "!");
                    binding.transferSwitch.setChecked("standard".equals(user.getUnitSystem()));
                    binding.creatineSwitch.setChecked(user.getUseCreatine());
                    binding.workoutLevelSpinner.setSelection(getWorkoutIndex(user.getWorkoutLevel()));

                    setupViewByUnit();
                    prefillHeightWeight();
                } else {
                    Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void prefillHeightWeight() {
        if (user == null) return;

        if ("standard".equals(user.getUnitSystem())) {
            double totalInches = user.getHeight() / 2.54;
            int feet = (int) (totalInches / 12);
            int inches = (int) (totalInches % 12);

            double weightLbs = user.getWeight() / 0.453592;

            binding.etUserFeet.setText(String.valueOf(feet));
            binding.etUserInches.setText(String.valueOf(inches));
            binding.etUserWeight.setText(String.format("%.1f", weightLbs));
        } else {
            binding.etUserHeightCm.setText(String.format("%.1f", user.getHeight()));
            binding.etUserWeight.setText(String.format("%.1f", user.getWeight()));
        }
    }

    private double calculateWaterGoal(User user) {
        if (user == null) return 0;

        double waterGoalLiters = user.getWeight() * 0.033;

        if (user.getUseCreatine()) {
            waterGoalLiters *= 1.3;
        }

        switch (user.getWorkoutLevel()) {
            case "High":
                waterGoalLiters *= 1.115;
                break;
            case "Moderate":
                break;
            case "Light":
                waterGoalLiters *= 0.885;
                break;
            case "Sedentary":
                waterGoalLiters *= 0.77;
                break;
            default:
                break;
        }

        return waterGoalLiters;
    }

    private int getWorkoutIndex(String level) {
        switch (level) {
            case "High": return 0;
            case "Light": return 2;
            case "Sedentary": return 3;
            default: return 1;
        }
    }

    private void saveChanges() {
        double heightCm = 0;
        double weightKg = 0;

        try {
            if ("standard".equals(user.getUnitSystem())) {
                String feetText = binding.etUserFeet.getText().toString().trim();
                String inchesText = binding.etUserInches.getText().toString().trim();
                String weightText = binding.etUserWeight.getText().toString().trim();

                if (feetText.isEmpty() || inchesText.isEmpty() || weightText.isEmpty()) {
                    Toast.makeText(this, "Please fill feet, inches, and weight.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double feet = Double.parseDouble(feetText);
                double inches = Double.parseDouble(inchesText);

                double totalInches = (feet * 12) + inches;
                heightCm = totalInches * 2.54;
                weightKg = Double.parseDouble(weightText) * 0.453592;
            } else {
                String heightText = binding.etUserHeightCm.getText().toString().trim();
                String weightText = binding.etUserWeight.getText().toString().trim();

                if (heightText.isEmpty() || weightText.isEmpty()) {
                    Toast.makeText(this, "Please fill height and weight.", Toast.LENGTH_SHORT).show();
                    return;
                }

                heightCm = Double.parseDouble(heightText);
                weightKg = Double.parseDouble(weightText);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input format.", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setHeight(heightCm);
        user.setWeight(weightKg);
        user.setUseCreatine(binding.creatineSwitch.isChecked());

        String selectedWorkoutLevel = binding.workoutLevelSpinner.getSelectedItem().toString();
        user.setWorkoutLevel(selectedWorkoutLevel);

        binding.btnEditProfile.setEnabled(false);

        executor.execute(() -> {
            AppDatabase.getInstance(this).userDao().updateUser(user);

            runOnUiThread(() -> {
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                binding.btnEditProfile.setEnabled(true);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
