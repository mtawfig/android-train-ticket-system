package org.feup.cmov.userticketapp.Models;

import android.content.ContentValues;
import android.content.SharedPreferences;

import java.util.Map;

public abstract class SharedPreferencesFactory {

    /*public static void setStringValueToPreferences(String key, String value, SharedPreferences sharedPreferences) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }*/

    public static void setBooleanValueToPreferences(String key, boolean value, SharedPreferences sharedPreferences) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setValuesToPreferences(ContentValues values, SharedPreferences sharedPreferences) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (Map.Entry<String, Object> entry : values.valueSet()) {
            editor.putString(entry.getKey(), entry.getValue().toString());
        }

        editor.apply();
    }

    public static String getStringValueFromPreferences(String key, SharedPreferences sharedPreferences) {

        if (sharedPreferences.contains(key))
            return sharedPreferences.getString(key, null);
        else
            return null;
    }

    public static Long getLongValueFromPreferences(String key, SharedPreferences sharedPreferences) {

        if (sharedPreferences.contains(key))
            return sharedPreferences.getLong(key, 0L);
        else
            return null;
    }

    public static boolean getBooleanValueFromPreferences(String key, SharedPreferences sharedPreferences) {
        return sharedPreferences.contains(key) && sharedPreferences.getBoolean(key, false);
    }

    public static void clearPreferences(SharedPreferences sharedPreferences) {

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        edit.apply();
    }

    public static void setLongValueToPreferences(String key, Long value, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }
}