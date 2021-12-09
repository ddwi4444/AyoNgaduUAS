package com.example.ayongadu.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ayongadu.database.entity.Ambulance;
import com.example.ayongadu.database.entity.Complain;

import java.util.List;

@Dao
public interface AppDao {
    @Query("SELECT * FROM complain")
    List<Complain> getComplainList();

    @Insert
    void insertComplain(Complain complain);

    @Delete
    void deleteComplain(Complain complain);

    @Insert
    void insertAmbulance(Ambulance ambulance);
}
