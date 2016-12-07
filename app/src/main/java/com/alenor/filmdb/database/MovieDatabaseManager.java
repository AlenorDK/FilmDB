package com.alenor.filmdb.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class MovieDatabaseManager {

    private static MovieDatabaseManager instance;
    private static volatile MovieDatabaseHelper dbHelper;

    public static synchronized MovieDatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new MovieDatabaseManager(context);
        }
        return instance;
    }

    public MovieDatabaseManager(Context context) {
        dbHelper = OpenHelperManager.getHelper(context, MovieDatabaseHelper.class);
    }

    public MovieDatabaseHelper getDbHelper() {
        return dbHelper;
    }
}
