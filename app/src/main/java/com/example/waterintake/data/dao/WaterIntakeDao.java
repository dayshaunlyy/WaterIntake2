package com.example.waterintake.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.waterintake.data.entities.WaterIntake;

import java.util.List;

@Dao
public interface WaterIntakeDao {

    // Insert a new water intake record
    @Insert
    void insert(WaterIntake waterIntake);

    // Get all water intake records for a specific user
    @Query("SELECT * FROM water_intakes WHERE userId = :userId")
    List<WaterIntake> getWaterIntakesForUser(int userId);
}
