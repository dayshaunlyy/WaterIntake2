package com.example.waterintake.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.waterintake.data.AppDatabase;
import com.example.waterintake.data.dao.UserDao;
import com.example.waterintake.data.entities.User;
import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserDao userDao;
    private final LiveData<List<User>> allUsers;

    public UserViewModel(Application application) {
        super(application);
        userDao = AppDatabase.getInstance(application).userDao();
        allUsers = userDao.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void deleteUser(User user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userDao.deleteUser(user);
        });
    }
}