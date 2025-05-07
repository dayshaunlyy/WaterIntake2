package com.example.waterintake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDashboardActivity extends AppCompatActivity {

    private EditText etUserHeightCm, etUserWeight;
    private Spinner workoutLevelSpinner;
    private Switch creatineSwitch;
    private TextView tvUserName;
    private Button btnSave;
    private int userId;
    private User user;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        etUserHeightCm = findViewById(R.id.etUserHeightCm);
        etUserWeight = findViewById(R.id.etUserWeight);
        workoutLevelSpinner = findViewById(R.id.workoutLevelSpinner);
        creatineSwitch = findViewById(R.id.creatineSwitch);
        tvUserName = findViewById(R.id.tvUserName);
        btnSave = findViewById(R.id.btnSave);

        String[] workoutOptions = {"Sedentary", "Light", "Moderate", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, workoutOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutLevelSpinner.setAdapter(adapter);

        userId = getIntent().getIntExtra("userId", -1);
        if (userId != -1) {
            executor.execute(() -> {
                user = AppDatabase.getInstance(this).userDao().getUserByIdSync(userId);
                if (user != null) {
                    runOnUiThread(() -> {
                        tvUserName.setText(user.getUsername());
                        etUserHeightCm.setText(String.valueOf(user.getHeight()));
                        etUserWeight.setText(String.valueOf(user.getWeight()));
                        creatineSwitch.setChecked(user.isUseCreatine());
                        int index = adapter.getPosition(user.getWorkoutLevel());
                        workoutLevelSpinner.setSelection(index);
                    });
                }
            });
        }

        btnSave.setOnClickListener(v -> {
            double height = Double.parseDouble(etUserHeightCm.getText().toString());
            double weight = Double.parseDouble(etUserWeight.getText().toString());
            String level = workoutLevelSpinner.getSelectedItem().toString();
            boolean useCreatine = creatineSwitch.isChecked();

            user.setHeight(height);
            user.setWeight(weight);
            user.setWorkoutLevel(level);
            user.setUseCreatine(useCreatine);

            executor.execute(() -> AppDatabase.getInstance(this).userDao().updateUser(user));
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_input) {
                Intent intent = new Intent(this, WaterInputActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_progress) {
                Intent intent = new Intent(this, ProgressActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                return true;
            }
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
