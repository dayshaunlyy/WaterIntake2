package com.example.waterintake;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText usernameInput;
    Button loginButton, logoutButton, adminButton;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.usernameInput);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);
        adminButton = findViewById(R.id.adminButton);

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        String savedUser = prefs.getString("username", null);
        boolean isAdmin = prefs.getBoolean("isAdmin", false);
        if (savedUser != null) {
            updateUI(true, isAdmin);
        }

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim().toLowerCase();
            hideKeyboard();//this is how to hide a key board, nice

            if (username.equals("testuser1")) {
                prefs.edit().putString("username", "testuser1").putBoolean("isAdmin", false).apply();
                updateUI(true, false);
                Toast.makeText(this, "Logged in as testuser1", Toast.LENGTH_SHORT).show();
            } else if (username.equals("admin2")) {
                prefs.edit().putString("username", "admin2").putBoolean("isAdmin", true).apply();
                updateUI(true, true);
                Toast.makeText(this, "Logged in as admin2", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid user: must be testuser1 or admin2", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            updateUI(false, false);
            usernameInput.setText("");
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(boolean loggedIn, boolean isAdmin) {
        loginButton.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        logoutButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        adminButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        usernameInput.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
