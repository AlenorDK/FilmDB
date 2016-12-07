package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieListContainer {

    private int page;

    private List<MovieList> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public MovieListContainer(int page, List<MovieList> results, int totalPages, int totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public int getPage() {
        return page;
    }

    public List<MovieList> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
