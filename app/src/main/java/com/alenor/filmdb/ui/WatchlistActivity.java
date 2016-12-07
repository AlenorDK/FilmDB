package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.Loader;

import com.alenor.filmdb.R;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.loader.WatchlistLoader;

public class WatchlistActivity extends MyListsBaseActivity {

    public static void start(Context context) {
        Intent i = new Intent(context, WatchlistActivity.class);
        context.startActivity(i);
    }

    @StringRes
    private static final int WATCHLIST_ACTIVITY_TITLE = R.string.watchlist_activity_title;
    private static final int WATCHLIST_LOADER_ID = 1;

    @Override
    protected int titleResId() {
        return WATCHLIST_ACTIVITY_TITLE;
    }

    @Override
    protected int loaderId() {
        return WATCHLIST_LOADER_ID;
    }

    @Override
    public Loader<MovieContainer> onCreateLoader(int id, Bundle args) {
        return new WatchlistLoader(this, MovieDatabaseManager.getInstance(this).getDbHelper().getWatchlistDao());
    }
}
