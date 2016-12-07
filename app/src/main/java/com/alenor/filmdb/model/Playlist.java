package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class Playlist {

    private String description;

    @SerializedName("id")
    private String playlistId;

    @SerializedName("item_count")
    private int itemCount;

    private String name;

    @SerializedName("poster_path")
    private String posterPath;

    public Playlist(String description, String playlistId, int itemCount, String name, String posterPath) {
        this.description = description;
        this.playlistId = playlistId;
        this.itemCount = itemCount;
        this.name = name;
        this.posterPath = posterPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
