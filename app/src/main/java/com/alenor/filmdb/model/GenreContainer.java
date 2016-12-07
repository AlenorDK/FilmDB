package com.alenor.filmdb.model;

import java.util.List;

public class GenreContainer {
    private List<Genre> genres;

    public GenreContainer(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }
}
