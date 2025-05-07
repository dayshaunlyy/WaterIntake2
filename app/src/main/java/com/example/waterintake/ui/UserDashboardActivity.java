package com.example.waterintake.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", true);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserDetails(userId);

        // ✅ Set up Workout Level Spinner Adapter
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                R.id.spinnerText,
                getResources().getStringArray(R.array.workout_levels)
        ) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Standard spinner item view
                View view = super.getView(position, convertView, parent);
                String level = getItem(position).toString();
                int color = getColorForLevel(level);
                view.setBackgroundColor(color); // Set background color based on workout level
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // Drop-down view (the list shown when clicking the spinner)
                View view = super.getDropDownView(position, convertView, parent);
                String level = getItem(position).toString();
                int color = getColorForLevel(level);
                view.setBackgroundColor(color); // Set background color for each dropdown item
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_item);
        binding.workoutLevelSpinner.setAdapter(adapter);


        binding.btnEditProfile.setOnClickListener(v -> saveChanges());
        binding.btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class); // Replace with your login activity class
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish();
        });

        setupSeekBar();
        setupCreatineSwitch();
        setupWorkoutSpinner();



        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

    }

    private boolean isSpinnerInitialized = false;

    private void setupWorkoutSpinner() {
        binding.workoutLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerInitialized) {
                    String selectedWorkoutLevel = parent.getItemAtPosition(position).toString();
                    Toast.makeText(UserDashboardActivity.this, "Workout Level: " + selectedWorkoutLevel, Toast.LENGTH_SHORT).show();
                } else {
                    isSpinnerInitialized = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }



    private void setupSeekBar() {
        binding.transferSwitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    if (user != null) user.setUnitSystem("standard");
                } else {
                    if (user != null) user.setUnitSystem("metric");
                }
                setupViewByUnit();
                prefillHeightWeight();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupCreatineSwitch() {
        binding.creatineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (user != null) {
                user.setUseCreatine(isChecked);
            }

            if (isChecked) {
                binding.creatineLogo.setVisibility(View.VISIBLE);
                binding.nocreatineLogo.setVisibility(View.GONE); // ❌ Hide the other
                binding.creatineLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            } else {
                binding.nocreatineLogo.setVisibility(View.VISIBLE);
                binding.creatineLogo.setVisibility(View.GONE); // ❌ Hide the other
                binding.nocreatineLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
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
                    binding.transferSwitch.setProgress("standard".equals(user.getUnitSystem()) ? 0 : 1);
                    binding.workoutLevelSpinner.setSelection(getWorkoutIndex(user.getWorkoutLevel()));
                    binding.creatineSwitch.setChecked(user.getUseCreatine());
                    if (user.getUseCreatine()) {
                        binding.creatineLogo.setVisibility(View.VISIBLE);
                        binding.nocreatineLogo.setVisibility(View.GONE);
                    } else {
                        binding.nocreatineLogo.setVisibility(View.VISIBLE);
                        binding.creatineLogo.setVisibility(View.GONE);
                    }



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
            case "High (6–7 times a week)":
                waterGoalLiters *= 1.115;
                break;

            case "Moderate (3–5 times a week)":
                break;

            case "Light (1–3 times a week)":
                waterGoalLiters *= 0.885;
                break;

            case "Sedentary (little/no exercise)":
                waterGoalLiters *= 0.77;
                break;

            default:
                break;
        }

        return waterGoalLiters;
    }

    private int getWorkoutIndex(String level) {
        switch (level) {
            case "High (6–7 times a week)": return 0;
            case "Moderate (3–5 times a week)": return 1;
            case "Light (1–3 times a week)": return 2;
            case "Sedentary (little/no exercise)": return 3;
            default: return 1;
        }
    }

    private int getColorForLevel(String level) {
        switch (level) {
            case "High (6–7 times a week)":
                return ContextCompat.getColor(this, R.color.workout_high);
            case "Moderate (3–5 times a week)":
                return ContextCompat.getColor(this, R.color.workout_moderate);
            case "Light (1–3 times a week)":
                return ContextCompat.getColor(this, R.color.workout_light);
            case "Sedentary (little/no exercise)":
                return ContextCompat.getColor(this, R.color.workout_sedentary);
            default:
                return ContextCompat.getColor(this, android.R.color.darker_gray); // fallback
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

                Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
                intent.putExtra("userId", user.getId()); // if MainActivity needs user ID
                startActivity(intent);
                finish();

            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}