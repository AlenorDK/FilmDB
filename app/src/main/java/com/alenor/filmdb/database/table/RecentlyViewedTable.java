package com.alenor.filmdb.database.table;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "RecentlyViewed")
public class RecentlyViewedTable {

    public static final int MAX_ITEM_COUNT = 6;

    @DatabaseField(columnName = "movieId", id = true, useGetSet = true)
    private long movieId;

    @DatabaseField(columnName = "movieTitle", useGetSet = true)
    private String movieTitle;

    @DatabaseField(columnName = "moviePoster", useGetSet = true)
    private String moviePoster;

    @DatabaseField(columnName = "viewedDate", useGetSet = true)
    private long viewedDate;

    public RecentlyViewedTable() {
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public long getViewedDate() {
        return viewedDate;
    }

    public void setViewedDate(long viewedDate) {
        this.viewedDate = viewedDate;
    }
}
