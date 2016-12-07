package com.alenor.filmdb.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Random;

public class MovieContainer {

    private long id;
    private int page;

    @SerializedName("results")
    private List<Movie> movies;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public MovieContainer() {
    }

    public MovieContainer(long id, int page, List<Movie> movies, int totalPages, int totalResults) {
        this.id = id;
        this.page = page;
        this.movies = movies;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    @Nullable
    public Movie getRandomMovie() {
        int movieCount = getMovies().size();
        Random random = new Random();
        return getMovies().get(random.nextInt(movieCount));
    }

    public long getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
