package com.example.waterintake.database;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.waterintake.database.entities.WaterIntake;

public interface WaterIntakeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WaterIntake waterIntake);
}
