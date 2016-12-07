package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.AccountActivity;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;
import com.alenor.filmdb.ui.MovieSearchFragment;

import java.io.IOException;

import retrofit2.Response;

public class FoundMoviesLoader extends BaseLoader<MovieContainer> {

    private String movieTitle;

    public FoundMoviesLoader(Context context, Bundle bundle) {
        super(context);
        movieTitle = bundle.getString(MovieSearchFragment.EXTRA_MOVIE_TITLE);
    }

    @Override
    public MovieContainer loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            if (!TextUtils.equals(movieTitle, "")) {
                boolean isAdultIncluded = SharedPrefUtils.getAdult(getContext());
                Response<MovieContainer> response = movieDBService.searchMovieByTitle(movieTitle, isAdultIncluded).execute();
                return response.body();
            } else {
                return new MovieContainer();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
