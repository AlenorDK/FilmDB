package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.Loader;

import com.alenor.filmdb.R;
import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.loader.FavoriteMoviesLoader;

public class FavoriteMoviesActivity extends MyListsBaseActivity {

    public static void start(Context context) {
        Intent i = new Intent(context, FavoriteMoviesActivity.class);
        context.startActivity(i);
    }

    @StringRes
    public static final int FAVORITE_MOVIES_ACTIVITY_TITLE = R.string.favorite_movies_activity_activity_title;
    private static final int FAVORITE_MOVIES_LOADER_ID = 0;

    @Override
    protected int titleResId() {
        return FAVORITE_MOVIES_ACTIVITY_TITLE;
    }

    @Override
    protected int loaderId() {
        return FAVORITE_MOVIES_LOADER_ID;
    }

    @Override
    public Loader<MovieContainer> onCreateLoader(int id, Bundle args) {
        return new FavoriteMoviesLoader(this, MovieDatabaseManager.getInstance(this).getDbHelper().getFavoritesDao());
    }
}
