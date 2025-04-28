package com.example.waterintake.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;

    private double height;  // Stored in cm
    private double weight;// Stored in kg

    private String unitSystem;// "standard" or "metric"

    private String workoutLevel; //"High", "Moderate", "Light", "Sedentary"

    private boolean useCreatine; // store true or false if user is taking creatine 1.30x increase if true

    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.unitSystem = "metric";
        this.useCreatine = false;
        this.workoutLevel = "Moderate";
        // Default to metric system
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getUnitSystem() { return unitSystem; }
    public void setUnitSystem(String unitSystem) { this.unitSystem = unitSystem; }

    public String getWorkoutLevel(){return workoutLevel;}
    public void setWorkoutLevel(String workoutLevel){this.workoutLevel= workoutLevel; }

    public boolean getUseCreatine() { return useCreatine; }
    public void setUseCreatine(boolean useCreatine) { this.useCreatine = useCreatine; }


}
