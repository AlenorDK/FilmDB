package com.alenor.filmdb.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {

    private static final String SESSION_PREFERENCES = "session_preferences";
    private static final int PREFERENCES_MODE = 0;

    private static final String SESSION_ID = "session_id";
    private static final String USERNAME = "username";
    private static final String ADULT = "adult";

    public static void setSessionId(Context context, String sessionId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SESSION_ID, sessionId);
        editor.apply();
    }

    public static String getSessionId(Context context) {
        SharedPreferences session = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        return session.getString(SESSION_ID, null);
    }

    public static void setUsername(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public static String getUsername(Context context) {
        SharedPreferences session = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        return session.getString(USERNAME, null);
    }

    public static void setAdult(Context context, boolean isAdult) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ADULT, isAdult);
        editor.apply();
    }

    public static boolean getAdult(Context context) {
        SharedPreferences session = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        return session.getBoolean(ADULT, false);
    }

    public static void clear(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static SharedPreferences.Editor edit(Context context) {
        SharedPreferences session = context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
        return session.edit();
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE);
    }

}
