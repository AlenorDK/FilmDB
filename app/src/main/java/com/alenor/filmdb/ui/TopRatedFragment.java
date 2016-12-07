package com.alenor.filmdb.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alenor.filmdb.R;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.adapter.GuestMovieListRecyclerViewAdapter;
import com.alenor.filmdb.ui.loader.TopRatedLoader;

public class TopRatedFragment extends Fragment {

    public static TopRatedFragment newInstance() {
        return new TopRatedFragment();
    }

    private static final int TOP_RATED_LOADER_ID = 0;

    private RecyclerView topRatedRecyclerView;
    private RecyclerView.Adapter topRatedRecyclerAdapter;
    private SwipeRefreshLayout contentLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.top_rated_fragment_layout, container, false);
        topRatedRecyclerView = (RecyclerView) v.findViewById(R.id.top_rated_fragment_movie_list);
        topRatedRecyclerView.setHasFixedSize(true);
        contentLayout = (SwipeRefreshLayout) v.findViewById(R.id.top_rated_fragment_refresh_layout);
        contentLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().getLoader(TOP_RATED_LOADER_ID).onContentChanged();
                contentLayout.setRefreshing(false);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TOP_RATED_LOADER_ID, null, new LoaderManager.LoaderCallbacks<MovieContainer>() {
            @Override
            public Loader<MovieContainer> onCreateLoader(int id, Bundle args) {
                return new TopRatedLoader(getContext());
            }

            @Override
            public void onLoadFinished(Loader<MovieContainer> loader, MovieContainer data) {
                if (data == null) {
                    Snackbar.make(contentLayout, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
                } else {
                    topRatedRecyclerAdapter = new GuestMovieListRecyclerViewAdapter(data);
                    topRatedRecyclerView.setAdapter(topRatedRecyclerAdapter);
                    topRatedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }


            @Override
            public void onLoaderReset(Loader<MovieContainer> loader) {

            }
        });
    }
}
