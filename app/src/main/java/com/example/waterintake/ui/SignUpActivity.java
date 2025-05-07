package com.example.waterintake.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.dao.UserDao;
import com.example.waterintake.data.entities.User;
import com.example.waterintake.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", true);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access DAO
        userDao = AppDatabase.getInstance(this).userDao();

        // Sign Up button logic
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(username, password);
                insertUser(newUser);
            }
        });

        // Back to Login button logic
        binding.btnBackToLogin.setOnClickListener(v -> finish());
    }

    // Insert user into the database
    private void insertUser(User user) {
        new Thread(() -> {
            userDao.insert(user);
            runOnUiThread(() -> {
                Toast.makeText(SignUpActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close SignUpActivity
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }
}
