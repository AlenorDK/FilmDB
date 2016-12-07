package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.FavoritesTable;
import com.alenor.filmdb.model.AccountInfo;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;

import retrofit2.Response;

public class FavoriteMoviesLoader extends BaseLoader<MovieContainer> implements Dao.DaoObserver {

    private String sessionId;
    private Dao<FavoritesTable, Long> favoritesDao;

    public FavoriteMoviesLoader(Context context, Dao<FavoritesTable, Long> favoritesDao) {
        super(context);
        sessionId = SharedPrefUtils.getSessionId(context);
        if (favoritesDao == null) {
            throw new IllegalStateException("Dao is not initialized");
        } else {
            this.favoritesDao = favoritesDao;
        }
    }

    @Override
    public MovieContainer loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<AccountInfo> accountIdResponse = movieDBService.getAccountInfo(sessionId).execute();
            long accountId = accountIdResponse.body().getAccountId();
            Response<MovieContainer> response = movieDBService.getFavoriteMovies(accountId, sessionId).execute();
            MovieContainer movieContainer = response.body();

            favoritesDao.unregisterObserver(this);

            Dao<FavoritesTable, Long> favoritesDao = MovieDatabaseManager.getInstance(getContext()).getDbHelper().getFavoritesDao();
            for (Movie movie : movieContainer.getMovies()) {
                FavoritesTable row = new FavoritesTable();
                row.setMovieId(movie.getId());
                row.setIsFavorite(true);
                try {
                    favoritesDao.createOrUpdate(row);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            favoritesDao.registerObserver(this);
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
        favoritesDao.unregisterObserver(this);
    }

    @Override
    public void onChange() {
        onContentChanged();
    }
}
