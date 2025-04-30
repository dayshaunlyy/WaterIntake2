package com.example.waterintake.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.waterintake.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // No buttons to manage or view reports anymore.
        // You can add more features here if necessary, like displaying stats or charts.
    }
}
