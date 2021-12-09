package com.example.ayongadu.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ayongadu.model.User;

public class AppSharedPreference {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public static final String PREFERENCE_NAME = "ayo_ngadu";
    public static final String IS_LOGIN = "is_login";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String PASSWORD = "password";

    public AppSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setIsLogin(boolean isLogin) {
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.commit();
    }

    public void setUser(int id, String name, String email, String phone, String password) {
        editor.putInt(ID, id);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHONE, phone);
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    public boolean isLogin() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public User getUser() {
        return new User(
                sharedPreferences.getInt(ID, -1),
                sharedPreferences.getString(NAME, ""),
                sharedPreferences.getString(EMAIL, ""),
                sharedPreferences.getString(PHONE, ""),
                sharedPreferences.getString(PASSWORD, "")
        );
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
