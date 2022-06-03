package com.example.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Database used to keep track of all exhibits added by user
 */
@Database(entities = {Exhibit.class}, exportSchema = false, version = 3)
public abstract class ExhibitDatabase extends RoomDatabase {
    private static ExhibitDatabase singleton = null;
    public abstract ExhibitDao exhibitDao();

    public synchronized static ExhibitDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ExhibitDatabase.makeDatabase(context);
        }
        return singleton;
    }

    /**
    * Database that can be used for testing
    */
    @VisibleForTesting
    public static void injectTestDatabase(ExhibitDatabase testDatabase) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = testDatabase;
    }

    private static ExhibitDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ExhibitDatabase.class, "exhibit.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }
}

