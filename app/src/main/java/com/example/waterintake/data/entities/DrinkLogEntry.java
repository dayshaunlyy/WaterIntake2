package com.example.waterintake.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "drink_log")
public class DrinkLogEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "amount_ml")
    private double amount;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @ColumnInfo(name = "date")
    private String date;

    public DrinkLogEntry(int userId, double amount, String timestamp, String date) {
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
