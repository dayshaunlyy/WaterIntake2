package com.example.waterintake.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.waterintake.data.entities.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    // Get user by username - synchronous (for Login)
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsernameSync(String username);

    // Get user by userId (background thread friendly)
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserByIdSync(int userId);

    // Optional: If you ever want LiveData (not used for now)
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    LiveData<User> getUserByIdLive(int userId);

    @Update
    void updateUser(User user);

    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUserById(int userId);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Delete
    void deleteUser(User user);
}
