package com.example.waterintake.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String password;
    private double height;
    private double weight;
    private boolean useCreatine;
    private String workoutLevel;
    private double totalIntake;
    private double wakingHours;
    private double hourlyIntake;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.height = 0.0;
        this.weight = 0.0;
        this.useCreatine = false;
        this.workoutLevel = "Moderate";
        this.totalIntake = 0.0;
        this.wakingHours = 0.0;
        this.hourlyIntake = 0.0;
    }

    private String unitSystem;

    public String getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(String unitSystem) {
        this.unitSystem = unitSystem;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isUseCreatine() {
        return useCreatine;
    }

    public void setUseCreatine(boolean useCreatine) {
        this.useCreatine = useCreatine;
    }

    public String getWorkoutLevel() {
        return workoutLevel;
    }

    public void setWorkoutLevel(String workoutLevel) {
        this.workoutLevel = workoutLevel;
    }

    public double getTotalIntake() {
        return totalIntake;
    }

    public void setTotalIntake(double totalIntake) {
        this.totalIntake = totalIntake;
    }

    public double getWakingHours() {
        return wakingHours;
    }

    public void setWakingHours(double wakingHours) {
        this.wakingHours = wakingHours;
    }

    public double getHourlyIntake() {
        return hourlyIntake;
    }

    public void setHourlyIntake(double hourlyIntake) {
        this.hourlyIntake = hourlyIntake;
    }


}
