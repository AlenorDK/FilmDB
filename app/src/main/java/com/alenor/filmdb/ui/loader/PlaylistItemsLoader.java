package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.PlaylistItemsContainer;
import com.alenor.filmdb.MovieDBApplication;
import com.alenor.filmdb.ui.PlaylistFragment;

import java.io.IOException;

import retrofit2.Response;

public class PlaylistItemsLoader extends BaseLoader<PlaylistItemsContainer> {

    private String playlistId;

    public PlaylistItemsLoader(Context context) {
        super(context);
        playlistId = SharedPrefUtils.getSessionId(getContext());
    }

    @Override
    public PlaylistItemsContainer loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<PlaylistItemsContainer> response = movieDBService.getPlaylist(playlistId).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
