package com.example.waterintake.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.time.LocalDateTime;

@Entity
public class WaterLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_id")
    public int userId;

    public int amount;

    public LocalDateTime timestamp;

    public WaterLog(int userId, int amount, LocalDateTime timestamp) {
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
