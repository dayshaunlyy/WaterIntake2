package com.example.waterintake.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Switch;


import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.R;

public class MainActivity extends AppCompatActivity {

    private EditText feetInput, inchInput, weightInput;
    private Switch unitToggle;
    private TextView resultTotal, resultHourly;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        feetInput = findViewById(R.id.etUserFeet);
        inchInput = findViewById(R.id.etUserInches);
        weightInput = findViewById(R.id.etUserWeight);
        unitToggle = findViewById(R.id.transferSwitch);

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> calculateWaterIntake());
    }

    private void calculateWaterIntake() {
        try {
            int feet = Integer.parseInt(feetInput.getText().toString());
            int inches = Integer.parseInt(inchInput.getText().toString());
            double weight = Double.parseDouble(weightInput.getText().toString());

            int heightInInches = (feet * 12) + inches;

            if (unitToggle.isChecked()) {
                // Convert kg to pounds
                weight = weight * 2.20462;
            }

            // Formula: weight (lbs) * 2/3 * 29.5735 = total mL per day
            double dailyIntakeML = weight * (2.0 / 3.0) * 29.5735;
            double hourlyIntakeML = dailyIntakeML / 24.0;

            resultTotal.setText(String.format("Total: %.0f mL/day", dailyIntakeML));
            resultHourly.setText(String.format("Hourly: %.0f mL/hour", hourlyIntakeML));

        } catch (Exception e) {
            resultTotal.setText("Please enter valid numbers.");
            resultHourly.setText("");
        }
    }
}
