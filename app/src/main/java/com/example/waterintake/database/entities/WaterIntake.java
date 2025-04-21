package com.example.waterintake.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "waterIntakeTable")
public class WaterIntake {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
}
