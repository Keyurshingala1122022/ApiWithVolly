package com.example.apiwithvolley;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.apiwithvolley.model.User;
import com.google.gson.Gson;

public class App extends Application {

    static Gson gson = new Gson();
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static final String USER = "USER";

    public static void putUser(User silkUser) {
        editor.putString(USER, gson.toJson(silkUser)).commit();
    }

    public static User getUser() {
        return gson.fromJson(preferences.getString(USER, ""), User.class);
    }

    //--------------------common methods-------------------------//
    public static void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public static String getString(String key) {
        return preferences.getString(key, "");
    }

    public static void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static void putInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return preferences.getInt(key, -1);
    }

    public static void putFloat(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    public static float getFloat(String key) {
        return preferences.getFloat(key, -1f);
    }

    public static void putLong(String key, long value) {
        editor.putLong(key, value).apply();
    }

    public static float getLong(String key) {
        return preferences.getLong(key, -1L);
    }
    //--------------------common methods-------------------------//


    public static void removeAll() {
        editor.clear().commit();
    }
}