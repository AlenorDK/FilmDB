package com.alenor.filmdb.ui.loader;


import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.ChangePlaylistBody;
import com.alenor.filmdb.model.StatusResponse;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;
import com.alenor.filmdb.ui.PlaylistFragment;

import java.io.IOException;

import retrofit2.Response;

public class ChangeMovieFromPlaylistLoader extends BaseLoader<StatusResponse> {

    private String playlistId;
    private String sessionId;
    private ChangePlaylistBody body;
    private int action;

    public ChangeMovieFromPlaylistLoader(Context context, Bundle bundle, ChangePlaylistBody body, int action) {
        super(context);
        playlistId = bundle.getString(PlaylistFragment.EXTRA_PLAYLIST_ID);
        sessionId = SharedPrefUtils.getSessionId(getContext());
        this.body = body;
        this.action = action;
    }

    @Override
    public StatusResponse loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            if (action == ChangePlaylistBody.ACTION_REMOVE) {
                Response<StatusResponse> response = movieDBService.removeMovieFromPlaylist(playlistId, sessionId, body).execute();
                return response.body();
            }
            if (action == ChangePlaylistBody.ACTION_ADD) {
                Response<StatusResponse> response = movieDBService.addMovieToPlaylist(playlistId, sessionId, body).execute();
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
