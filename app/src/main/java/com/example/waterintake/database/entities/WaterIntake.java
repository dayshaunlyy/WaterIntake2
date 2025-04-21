package com.example.waterintake.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = "waterIntakeTable")
public class WaterIntake {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String password;
    private LocalDateTime date;

    public WaterIntake(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "WaterIntake{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WaterIntake)) return false;
        WaterIntake that = (WaterIntake) o;
        return id == that.id && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, date);
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
