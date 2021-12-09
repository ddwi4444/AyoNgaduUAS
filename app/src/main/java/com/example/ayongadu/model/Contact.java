package com.example.ayongadu.model;

public class Contact {
    private String name;
    private String profession;
    private String number;

    public Contact(String name, String profession, String number) {
        this.name = name;
        this.profession = profession;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
