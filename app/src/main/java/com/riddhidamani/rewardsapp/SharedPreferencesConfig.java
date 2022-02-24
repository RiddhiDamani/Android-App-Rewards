package com.riddhidamani.rewardsapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesConfig {

    private static final String TAG = "SharedPreferencesConfig";
    private final SharedPreferences prefs;

    public SharedPreferencesConfig(Activity activity) {
        super();
        Log.d(TAG, "SharedPreferencesConfig: ");
        prefs = activity.getSharedPreferences("MY_PREFS_KEY", Context.MODE_PRIVATE);
    }

    void save(String key, String text) {
        Log.d(TAG, "save: " + key + ":" + text);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, text);
        editor.apply(); // commit T/F
    }

    String getValue(String key) {
        String text = prefs.getString(key, "");
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }


    void clearAll() {
        Log.d(TAG, "clearAll: ");
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // commit T/F
    }

    void removeValue(String key) {
        Log.d(TAG, "removeValue: " + key);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply(); // commit T/F
    }
}
