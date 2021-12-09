package com.example.ayongadu.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ayongadu.BR;

public class Complain extends BaseObservable {
    private int id;
    private String name;
    private String complain;
    private String date;
    private double latitude;
    private double longitude;

    public Complain() {
    }

    public Complain(String name, String complain, String date, double latitude, double longitude) {
        this.name = name;
        this.complain = complain;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
        notifyPropertyChanged(BR.complain);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
