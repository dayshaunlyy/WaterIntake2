package com.example.waterintake.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.waterintake.data.entities.DrinkLogEntry;

import java.util.List;

@Dao
public interface DrinkLogDao {

    @Insert
    void insert(DrinkLogEntry entry);

    @Query("SELECT * FROM drink_log WHERE user_id = :userId ORDER BY timestamp DESC")
    List<DrinkLogEntry> getLogsForUser(int userId);

    @Query("SELECT SUM(amount_ml) FROM drink_log WHERE user_id = :userId AND date(timestamp) = date('now')")
    Double getTodayIntake(int userId);
}
