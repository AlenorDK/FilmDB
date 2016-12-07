package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.MovieImagesContainer;
import com.alenor.filmdb.MovieDBApplication;

import java.io.IOException;

import retrofit2.Response;

public class BackdropsLoader extends BaseLoader<MovieImagesContainer> {

    public static final String BUNDLE_MOVIE_ID = "bundle_movie_id";
    private final long movieId;

    public BackdropsLoader(Context context, Bundle args) {
        super(context);
        movieId = args.getLong(BUNDLE_MOVIE_ID);
    }

    @Override
    public MovieImagesContainer loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<MovieImagesContainer> response = movieDBService.getMovieImagesByMovieId(movieId).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
