package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.WatchlistTable;
import com.alenor.filmdb.model.AccountInfo;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;

import retrofit2.Response;

public class WatchlistLoader extends BaseLoader<MovieContainer> implements Dao.DaoObserver {

    private String sessionId;
    private Dao<WatchlistTable, Long> watchlistDao;

    public WatchlistLoader(Context context, Dao<WatchlistTable, Long> watchlistDao) {
        super(context);
        sessionId = SharedPrefUtils.getSessionId(context);
        if (watchlistDao == null) {
            throw new IllegalStateException("Dao is not initialized");
        } else {
            this.watchlistDao = watchlistDao;
        }
    }

    @Override
    public MovieContainer loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<AccountInfo> accountIdResponse = movieDBService.getAccountInfo(sessionId).execute();
            long accountId = accountIdResponse.body().getAccountId();
            Response<MovieContainer> response = movieDBService.getWatchlist(accountId, sessionId).execute();
            MovieContainer movieContainer = response.body();

            watchlistDao.unregisterObserver(this);

            Dao<WatchlistTable, Long> watchlistDao = MovieDatabaseManager.getInstance(getContext()).getDbHelper().getWatchlistDao();
            for (Movie movie : movieContainer.getMovies()) {
                WatchlistTable row = new WatchlistTable();
                row.setMovieId(movie.getId());
                row.setIsInWatchlist(true);
                try {
                    watchlistDao.createOrUpdate(row);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            watchlistDao.registerObserver(this);
            return movieContainer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    protected void onReset() {
        super.onReset();
        watchlistDao.unregisterObserver(this);
    }

    @Override
    public void onChange() {
        onContentChanged();
    }
}
