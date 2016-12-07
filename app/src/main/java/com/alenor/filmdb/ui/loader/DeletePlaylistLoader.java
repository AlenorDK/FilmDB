package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.DeletePlaylistResponse;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;
import com.alenor.filmdb.ui.PlaylistFragment;

import java.io.IOException;

import retrofit2.Response;

public class DeletePlaylistLoader extends BaseLoader<DeletePlaylistResponse> {

    private String playlistId;
    private String sessionId;

    public DeletePlaylistLoader(Context context, Bundle bundle) {
        super(context);
        playlistId = bundle.getString(PlaylistFragment.EXTRA_PLAYLIST_ID);
        sessionId = SharedPrefUtils.getSessionId(getContext());
    }

    @Override
    public DeletePlaylistResponse loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<DeletePlaylistResponse> deleteResponse = movieDBService.deletePlaylist(playlistId, sessionId).execute();
            return deleteResponse.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
