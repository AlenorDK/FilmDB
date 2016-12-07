package com.alenor.filmdb.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PlaylistTable")
public class PlaylistTable {

    public static final String PLAYLIST_DESCRIPTION_COLUMN_NAME = "playlistDescription";

    @DatabaseField(columnName = "playlistId", id = true, useGetSet = true)
    String playlistId;

    @DatabaseField(columnName = "playlistName", useGetSet = true, canBeNull = false)
    String playlistName;

    @DatabaseField(columnName = "playlistDescription", useGetSet = true)
    String playlistDescription;

    public PlaylistTable() {
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistDescription() {
        return playlistDescription;
    }

    public void setPlaylistDescription(String playlistDescription) {
        this.playlistDescription = playlistDescription;
    }
}
