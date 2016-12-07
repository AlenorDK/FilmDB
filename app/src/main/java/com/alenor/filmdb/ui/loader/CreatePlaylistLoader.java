package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.CreatePlaylistBody;
import com.alenor.filmdb.model.CreatePlaylistResponse;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;

import java.io.IOException;

import retrofit2.Response;

public class CreatePlaylistLoader extends BaseLoader<CreatePlaylistResponse> {

    private String sessionId;
    private CreatePlaylistBody body;

    public CreatePlaylistLoader(Context context, CreatePlaylistBody body) {
        super(context);
        this.sessionId = SharedPrefUtils.getSessionId(getContext());
        this.body = body;
    }

    @Override
    public CreatePlaylistResponse loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<CreatePlaylistResponse> response = movieDBService.createPlaylist(sessionId, body).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
