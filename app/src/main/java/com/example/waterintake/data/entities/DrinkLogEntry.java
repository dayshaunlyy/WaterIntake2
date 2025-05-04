package com.example.waterintake.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.time.LocalDateTime;


// DrinkLogEntry.java
@Entity(tableName = "drink_log")
public class DrinkLogEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "amount_ml")
    private double amount;

    @ColumnInfo(name = "timestamp")
    private LocalDateTime timestamp;

    public DrinkLogEntry(int userId, double amount, LocalDateTime timestamp) {
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

