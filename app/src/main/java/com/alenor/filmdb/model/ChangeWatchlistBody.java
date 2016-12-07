package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class ChangeWatchlistBody {

    public static final String MEDIA_TYPE_MOVIE = "movie";
    public static final String MEDIA_TYPE_TV = "tv";
    public static final boolean ADD_TO_WATCHLIST = true;
    public static final boolean REMOVE_FROM_WATCHLIST = false;

    @SerializedName("media_type")
    String mediaType;

    @SerializedName("media_id")
    long mediaId;

    @SerializedName("watchlist")
    boolean isInWatchlist;

    public ChangeWatchlistBody(String mediaType, long mediaId, boolean isInWatchlist) {
        this.mediaType = mediaType;
        this.mediaId = mediaId;
        this.isInWatchlist = isInWatchlist;
    }

    public String getMediaType() {
        return mediaType;
    }

    public long getMediaId() {
        return mediaId;
    }

    public boolean isInWatchlist() {
        return isInWatchlist;
    }
}
