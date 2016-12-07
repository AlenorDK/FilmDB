package com.alenor.filmdb.ui.loader;

import android.content.Context;

import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.MovieDBApplication;

import java.io.IOException;

import retrofit2.Response;

public class TopRatedLoader extends BaseLoader<MovieContainer> {

    public TopRatedLoader(Context context) {
        super(context);
    }

    @Override
    public MovieContainer loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<MovieContainer> response = movieDBService.getTopRated().execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
