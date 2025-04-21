package com.example.waterintake.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.waterintake.MainActivity;
import com.example.waterintake.database.entities.WaterIntake;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = WaterIntake.class, version = 1, exportSchema = false)
public abstract class WaterIntakeDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "Water_Intake_database";
    public static final String WATER_INTAKE_TABLE = "waterIntakeTable";

    public static volatile WaterIntakeDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static WaterIntakeDatabase getDatabase(final Context context){
        if(INSTANCE == null){
        synchronized (WaterIntakeDatabase.class){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                WaterIntakeDatabase.class, DATABASE_NAME).fallbackToDestructiveMigrationFrom()
                        .addCallback(addDefaultValues)
                        .build();
            }
        }
    }
        return INSTANCE;
    }
    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            Log.i(MainActivity.TAG,"Database Created");
            // todo: add databaseWriteExecutor.execute(() -> {..}
        }
    };

    public abstract WaterIntakeDAO waterIntakeDAO();

}

