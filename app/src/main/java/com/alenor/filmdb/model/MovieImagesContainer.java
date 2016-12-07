package com.alenor.filmdb.model;

import java.util.List;

public class MovieImagesContainer {

    private long id;
    private List<MovieImages> backdrops;
    private List<MovieImages> posters;

    public MovieImagesContainer(long id, List<MovieImages> backdrops, List<MovieImages> posters) {
        this.id = id;
        this.backdrops = backdrops;
        this.posters = posters;
    }

    public long getId() {
        return id;
    }

    public List<MovieImages> getBackdrops() {
        return backdrops;
    }

    public List<MovieImages> getPosters() {
        return posters;
    }
}
