package com.example.waterintake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.DrinkLogEntry;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaterInputActivity extends AppCompatActivity {

    private EditText inputAmount;
    private Button btnLog;
    private int userId;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_input);

        inputAmount = findViewById(R.id.inputAmount);
        btnLog = findViewById(R.id.btnLog);
        userId = getIntent().getIntExtra("userId", -1);

        btnLog.setOnClickListener(v -> {
            String text = inputAmount.getText().toString().trim();
            if (!text.isEmpty()) {
                double amount = Double.parseDouble(text);

                executor.execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(this);
                    db.drinkLogDao().insert(new DrinkLogEntry(userId, amount, LocalDateTime.now()));

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Water intake logged!", Toast.LENGTH_SHORT).show();
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
}
