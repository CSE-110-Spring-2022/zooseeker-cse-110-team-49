package com.example.cse110_team49;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Exhibit.class}, version = 1)
public abstract class ExhibitDatabase extends RoomDatabase {
    private static ExhibitDatabase singleton = null;
    public abstract ExhibitDao exhibitDao();

    public synchronized static ExhibitDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ExhibitDatabase.makeDatabase(context);
        }
        return singleton;
    }

    @VisibleForTesting
    public static void injectTestDatabase(ExhibitDatabase testDatabase) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = testDatabase;
    }

    private static ExhibitDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ExhibitDatabase.class, "exhibit1.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<Exhibit> exhibits = Exhibit
                                    .loadJSON(context, "sample_node_info.json");
                            getSingleton(context).exhibitDao().insertAll(exhibits);
                        });
                    }
                })
                .build();
    }
}

