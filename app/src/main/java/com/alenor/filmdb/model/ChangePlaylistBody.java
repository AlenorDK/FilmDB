package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class ChangePlaylistBody {

    public static final int ACTION_ADD = 0;
    public static final int ACTION_REMOVE = 1;

    @SerializedName("media_id")
    private long movieId;

    public ChangePlaylistBody(long movieId) {
        this.movieId = movieId;
    }

    public long getMovieId() {
        return movieId;
    }
}
