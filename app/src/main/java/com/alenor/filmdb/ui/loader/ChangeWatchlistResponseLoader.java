package com.alenor.filmdb.ui.loader;

import android.content.Context;

import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.ChangeWatchlistBody;
import com.alenor.filmdb.model.ChangeWatchlistResponse;
import com.alenor.filmdb.MovieDBApplication;

import java.io.IOException;

import retrofit2.Response;

public class ChangeWatchlistResponseLoader extends BaseLoader<ChangeWatchlistResponse> {

    private ChangeWatchlistBody body;
    private String sessionId;


    public ChangeWatchlistResponseLoader(Context context, String sessionId, ChangeWatchlistBody body) {
        super(context);
        this.sessionId = sessionId;
        this.body = body;
    }

    @Override
    public ChangeWatchlistResponse loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<ChangeWatchlistResponse> response = movieDBService.changeWatchlist(sessionId, body).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
