package com.example.waterintake.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.DrinkLogEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inputAmount = findViewById(R.id.inputAmount);
        btnLog = findViewById(R.id.btnLog);
        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setSelectedItemId(R.id.nav_log_water);

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

        btnLog.setOnClickListener(v -> {
            String text = inputAmount.getText().toString().trim();
            if (!text.isEmpty()) {
                double amount = Double.parseDouble(text);

                executor.execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(this);

                    LocalDateTime now = LocalDateTime.now();
                    String timestamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    String date = now.toLocalDate().toString();

                    DrinkLogEntry entry = new DrinkLogEntry(userId, amount, timestamp, date);
                    db.drinkLogDao().insert(entry);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Water logged!", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("intakeAmount", amount);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                });
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
