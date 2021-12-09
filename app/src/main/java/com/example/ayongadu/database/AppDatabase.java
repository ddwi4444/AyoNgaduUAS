package com.example.ayongadu.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.ayongadu.database.entity.Ambulance;
import com.example.ayongadu.database.entity.Complain;

@Database(entities = {Ambulance.class, Complain.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao appDao();
}
