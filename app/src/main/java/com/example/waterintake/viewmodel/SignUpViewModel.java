package com.example.waterintake.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.entities.User;

public class SignUpViewModel extends AndroidViewModel {

    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    private final AppDatabase database;

    public SignUpViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    // Sign up logic with validation for username and password
    public void signUp() {
        String usernameValue = username.getValue();
        String passwordValue = password.getValue();

        // Check if username and password are valid
        if (usernameValue != null && passwordValue != null && !usernameValue.isEmpty() && !passwordValue.isEmpty()) {

            // Create a new user object
            User newUser = new User(usernameValue, passwordValue);

            // Insert the new user into the database
            new Thread(() -> {
                database.userDao().insert(newUser);
            }).start();
        } else {
            // Handle invalid input for username or password
            // You can use a LiveData or Toast to show an error message in the UI
            // Example: Toast.makeText(getApplication(), "Please fill in both username and password", Toast.LENGTH_SHORT).show();
        }
    }
}
