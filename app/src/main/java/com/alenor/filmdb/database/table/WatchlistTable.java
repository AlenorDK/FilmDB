package com.alenor.filmdb.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "WatchlistTable")
public class WatchlistTable {

    public static final String MOVIE_ID_FIELD_NAME = "movieId";

    @DatabaseField(columnName = "movieId", id = true, useGetSet = true)
    private long movieId;

    @DatabaseField(columnName = "isInWatchlist", defaultValue = "false", canBeNull = false, useGetSet = true)
    private boolean isInWatchlist;

    public WatchlistTable() {
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public boolean getIsInWatchlist() {
        return isInWatchlist;
    }

    public void setIsInWatchlist(boolean inWatchlist) {
        isInWatchlist = inWatchlist;
    }
}
