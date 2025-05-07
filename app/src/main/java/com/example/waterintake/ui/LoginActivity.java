package com.example.waterintake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.waterintake.AdminUtils;
import com.example.waterintake.R;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private static final String TAG = "LoginActivity"; // ✅ for logging errors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);

        findViewById(R.id.loginBtn).setOnClickListener(v -> login());

        findViewById(R.id.signupRedirect).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class))
        );
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.equals(AdminUtils.ADMIN_USERNAME) && password.equals(AdminUtils.ADMIN_PASSWORD)) {
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        new Thread(() -> {
            try {
                User user = AppDatabase.getInstance(LoginActivity.this)
                        .userDao()
                        .getUserByUsernameSync(username);

                runOnUiThread(() -> {
                    if (user != null) {
                        if (user.getPassword().equals(password)) {
                            Log.e(TAG, "Login successful for userId: " + user.getId());

                            // ✅ Check last screen and userId in SharedPreferences
                            String lastScreen = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                                    .getString("lastScreen", "");
                            int lastUserId = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                                    .getInt("lastUserId", -1);

                            Intent intent;
                            if ("progress".equals(lastScreen) && lastUserId == user.getId()) {
                                intent = new Intent(LoginActivity.this, ProgressActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                            }

                            intent.putExtra("userId", user.getId());
                            startActivity(intent);
                            finish();

                        } else {
                            Log.e(TAG, "Incorrect password for username: " + username);
                            Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "No user found with username: " + username);
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error during login", e);
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

}

