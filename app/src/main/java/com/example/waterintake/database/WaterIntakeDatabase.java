package com.example.waterintake.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.waterintake.database.dao.WaterIntakeDAO;
import com.example.waterintake.database.entities.WaterIntake;
import com.example.waterintake.util.LocalDateTimeConverter;

@Database(entities = {WaterIntake.class}, version = 1, exportSchema = false)
@TypeConverters({LocalDateTimeConverter.class})
public abstract class WaterIntakeDatabase extends RoomDatabase {

    private static final String DB_NAME = "water_intake.db";
    private static WaterIntakeDatabase instance;

    public static synchronized WaterIntakeDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            WaterIntakeDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract WaterIntakeDAO waterIntakeDAO();
}
