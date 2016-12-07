package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistContainer {

    int page;

    List<Playlist> results;

    @SerializedName("total_pages")
    int totalPages;

    @SerializedName("total_results")
    int totalResults;

    public PlaylistContainer(int page, List<Playlist> results, int totalPages, int totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public int getPage() {
        return page;
    }

    public List<Playlist> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
