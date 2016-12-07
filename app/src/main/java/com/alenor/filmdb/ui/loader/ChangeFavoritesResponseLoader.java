package com.alenor.filmdb.ui.loader;

import android.content.Context;

import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.ChangeFavoritesBody;
import com.alenor.filmdb.model.ChangeFavoritesResponse;
import com.alenor.filmdb.MovieDBApplication;

import java.io.IOException;

import retrofit2.Response;

public class ChangeFavoritesResponseLoader extends BaseLoader<ChangeFavoritesResponse> {

    private ChangeFavoritesBody body;
    private String sessionId;

    public ChangeFavoritesResponseLoader(Context context, String sessionId, ChangeFavoritesBody body) {
        super(context);
        this.body = body;
        this.sessionId = sessionId;
    }

    @Override
    public ChangeFavoritesResponse loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<ChangeFavoritesResponse> response = movieDBService.changeFavorites(sessionId, body).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
