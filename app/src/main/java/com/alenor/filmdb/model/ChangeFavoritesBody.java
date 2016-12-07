package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class ChangeFavoritesBody {

    public static final String MEDIA_TYPE_MOVIE = "movie";
    public static final String MEDIA_TYPE_TV = "tv";
    public static final boolean ADD_TO_FAVORITES = true;
    public static final boolean REMOVE_FROM_FAVORITES = false;



    @SerializedName("media_type")
    String mediaType;

    @SerializedName("media_id")
    long mediaId;

    @SerializedName("favorite")
    boolean isFavorite;

    public ChangeFavoritesBody(String mediaType, long mediaId, boolean action) {
        this.mediaType = mediaType;
        this.mediaId = mediaId;
        this.isFavorite = action;
    }

    public String getMediaType() {
        return mediaType;
    }

    public long getMediaId() {
        return mediaId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
