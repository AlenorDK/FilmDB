package com.alenor.filmdb.database.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "FavoritesTable")
public class FavoritesTable {

    public static final String MOVIE_ID_FIELD_NAME = "movieId";

    @DatabaseField(columnName = "movieId", id = true, useGetSet = true)
    private long movieId;

    @DatabaseField(columnName = "isFavorite", defaultValue = "false", canBeNull = false, useGetSet = true)
    private boolean isFavorite;

    public FavoritesTable() {
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
