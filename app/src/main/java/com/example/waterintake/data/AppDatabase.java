package com.example.waterintake.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.waterintake.data.dao.UserDao;
import com.example.waterintake.data.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 3, exportSchema = false)// ✅ bump version to 2
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "water_intake_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // ✅ Add both migrations
                            .addCallback(roomCallback)
                            .build();

                }
            }
        }
        return instance;
    }

    // Room callback to prepopulate database
    private static final Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                UserDao userDao = instance.userDao();

                User sampleUser = new User("testuser", "testpass");
                sampleUser.setHeight(175.0);
                sampleUser.setWeight(70.0);
                sampleUser.setUnitSystem("metric");
                sampleUser.setWorkoutLevel("Moderate");
                sampleUser.setUseCreatine(false);


                userDao.insert(sampleUser);
            });
        }
    };

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN useCreatine INTEGER NOT NULL DEFAULT 0");
        }
    };

//migration for workout level
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN workoutLevel TEXT DEFAULT 'Moderate'");
        }
    };


}
