package com.example.waterintake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.WaterIntake;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaterInputActivity extends AppCompatActivity {

    private EditText inputAmount;
    private Button btnLog;
    private BottomNavigationView bottomNav;

    private int userId;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_input);

        inputAmount = findViewById(R.id.inputAmount);
        btnLog = findViewById(R.id.btnLog);
        bottomNav = findViewById(R.id.bottomNav);

        userId = getIntent().getIntExtra("userId", -1);

        btnLog.setOnClickListener(v -> {
            String input = inputAmount.getText().toString().trim();
            if (!input.isEmpty()) {
                double amount = Double.parseDouble(input);
                WaterIntake entry = new WaterIntake(amount, LocalDate.now().toString(), userId);
                executor.execute(() -> AppDatabase.getInstance(this).waterIntakeDao().insert(entry));
                Toast.makeText(this, "Logged " + amount + " mL", Toast.LENGTH_SHORT).show();
                inputAmount.setText("");
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(this, UserDashboardActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_progress) {
                Intent intent = new Intent(this, ProgressActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Optional: Highlight the current nav item if you have nav_input defined
        bottomNav.setSelectedItemId(R.id.nav_input);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
