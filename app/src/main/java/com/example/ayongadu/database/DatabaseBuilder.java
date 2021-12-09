package com.example.ayongadu.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseBuilder {
    private Context context;
    private static DatabaseBuilder databaseBuilder;

    private AppDatabase database;

    public DatabaseBuilder(Context context) {
        this.context = context;
        database = Room.databaseBuilder(context, AppDatabase.class, "ayo_ngadu.db")
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized DatabaseBuilder getInstance(Context context) {
        if (databaseBuilder == null) {
            databaseBuilder = new DatabaseBuilder(context);
        }
        return databaseBuilder;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
