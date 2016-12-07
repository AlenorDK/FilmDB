package com.alenor.filmdb.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alenor.filmdb.MovieDBApplication;
import com.alenor.filmdb.R;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.adapter.GenresRecyclerViewAdapter;

import java.util.Random;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GenreFragment extends Fragment {

    public static GenreFragment newInstance() {
        return new GenreFragment();
    }

    private RecyclerView genresRecyclerView;
    private RecyclerView.Adapter genresRecyclerAdapter;
    private SwipeRefreshLayout contentLayout;
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.genres_fragment_layout, container, false);
        genresRecyclerView = (RecyclerView) v.findViewById(R.id.genres_fragment_genres_list);
        genresRecyclerView.setHasFixedSize(true);
        contentLayout = (SwipeRefreshLayout) v.findViewById(R.id.genres_fragment_layout_refresh_layout);
        contentLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGenresList();
                contentLayout.setRefreshing(false);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadGenresList();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void loadGenresList() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        subscription = movieDBService.getGenresList()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(genreContainer -> {
                    genresRecyclerAdapter = new GenresRecyclerViewAdapter(genreContainer, genreId -> {
                        Observable<MovieContainer> getMoviesList =
                                movieDBService.getMovieByGenreId(genreId, null);
                        getMoviesList.subscribeOn(Schedulers.newThread())
                                .subscribe(movieContainer -> {
                                    int pageCount = movieContainer.getTotalPages();
                                    int pageNumber = new Random().nextInt(pageCount <= 1000 ? pageCount : 1000);
                                    Observable<MovieContainer> getRandomMovie =
                                            movieDBService.getMovieByGenreId(genreId, pageNumber);
                                    getRandomMovie.subscribeOn(Schedulers.newThread())
                                            .subscribe(data -> {
                                                long movieId = data.getRandomMovie().getId();
                                                MovieActivity.startAsGuest(getContext(), movieId);
                                            });
                                });
                    });
                    genresRecyclerView.setAdapter(genresRecyclerAdapter);
                    genresRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }, e -> {
                    Snackbar.make(contentLayout, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
                });
    }
}
