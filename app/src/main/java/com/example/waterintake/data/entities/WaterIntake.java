package com.example.waterintake.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        tableName = "water_intakes",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE  // Delete water intake records if user is deleted
        )
)
public class WaterIntake {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WaterIntake)) return false;
        WaterIntake that = (WaterIntake) o;
        return id == that.id && Double.compare(waterAmount, that.waterAmount) == 0 && userId == that.userId && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, waterAmount, date, userId);
    }

    @PrimaryKey(autoGenerate = true)
    public int id;  // Primary key for the water intake record

    public double waterAmount;  // Amount of water consumed
    public String date;  // Date of water intake

    public int userId;  // Foreign key to the User table

    // Constructor
    public WaterIntake(double waterAmount, String date, int userId) {
        this.waterAmount = waterAmount;
        this.date = date;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getWaterAmount() {
        return waterAmount;
    }

    public void setWaterAmount(double waterAmount) {
        this.waterAmount = waterAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
