package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.database.MovieDatabaseHelper;
import com.alenor.filmdb.database.table.FavoritesTable;
import com.alenor.filmdb.database.table.WatchlistTable;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.adapter.MyListsPreviewAdapter;
import com.alenor.filmdb.ui.loader.FavoriteMoviesLoader;
import com.alenor.filmdb.ui.loader.WatchlistLoader;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class MyListsFragment extends Fragment {

    public static MyListsFragment newInstance() {
        return new MyListsFragment();
    }

    private static final int FAVORITE_MOVIES_LOADER_ID = 0;
    private static final int WATCHLIST_LOADER_ID = 1;

    private RecyclerView favoriteMoviesRecyclerView;
    private RecyclerView watchlistRecyclerView;
    private TextView emptyFavoritesText;
    private TextView emptyWatchlistText;
    private TextView favoritesMoreText;
    private TextView watchlistMoreText;
    private ProgressBar favoritesProgressBar;
    private ProgressBar watchlistProgressBar;
    private String sessionId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_lists_fragment_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoriteMoviesRecyclerView = (RecyclerView) view.findViewById(R.id.my_lists_fragment_layout_playlist_list);
        watchlistRecyclerView = (RecyclerView) view.findViewById(R.id.my_lists_fragment_layout_watchlist_list);
        emptyFavoritesText = (TextView) view.findViewById(R.id.my_lists_fragment_layout_favorites_empty_list_label);
        emptyWatchlistText = (TextView) view.findViewById(R.id.my_lists_fragment_layout_watchlist_empty_list_label);
        sessionId = SharedPrefUtils.getSessionId(getContext());

        favoritesProgressBar = (ProgressBar) view.findViewById(R.id.my_lists_fragment_layout_favorites_progress_bar);
        watchlistProgressBar = (ProgressBar) view.findViewById(R.id.my_lists_fragment_layout_watchlist_progress_bar);

        favoritesMoreText = (TextView) view.findViewById(R.id.my_lists_fragment_layout_favorites_menu_text_button);
        favoritesMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteMoviesActivity.start(getContext());
            }
        });

        watchlistMoreText = (TextView) view.findViewById(R.id.my_lists_fragment_layout_watchlist_menu_text_button);
        watchlistMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WatchlistActivity.start(getContext());
            }
        });

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.my_lists_fragment_layout_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    favoritesProgressBar.setVisibility(View.VISIBLE);
                    watchlistProgressBar.setVisibility(View.VISIBLE);
                    initFavoriteMoviesLoader();
                    initWatchlistLoader();
                } else {
                    Snackbar.make(favoriteMoviesRecyclerView, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOnline()) {
            initFavoriteMoviesLoader();
            initWatchlistLoader();
        } else {
            favoritesProgressBar.setVisibility(View.GONE);
            watchlistProgressBar.setVisibility(View.GONE);
            Snackbar.make(favoriteMoviesRecyclerView, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initFavoriteMoviesLoader() {
        getLoaderManager().initLoader(FAVORITE_MOVIES_LOADER_ID, getArguments(), new LoaderManager.LoaderCallbacks<MovieContainer>() {
            @Override
            public Loader<MovieContainer> onCreateLoader(int id, Bundle args) {
                Dao<FavoritesTable, Long> favoritesDao = OpenHelperManager.getHelper(getContext(), MovieDatabaseHelper.class).getFavoritesDao();
                return new FavoriteMoviesLoader(getContext(), favoritesDao);
            }

            @Override
            public void onLoadFinished(Loader<MovieContainer> loader, MovieContainer data) {
                if (data != null) {
                    if (data.getMovies().size() != 0) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        favoriteMoviesRecyclerView.setAdapter(new MyListsPreviewAdapter(data.getMovies(), sessionId));
                        favoriteMoviesRecyclerView.setLayoutManager(layoutManager);
                        setContentVisibility(true, favoriteMoviesRecyclerView, favoritesMoreText, emptyFavoritesText);
                    } else {
                        setContentVisibility(false, favoriteMoviesRecyclerView, favoritesMoreText, emptyFavoritesText);
                    }
                }
                favoritesProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoaderReset(Loader<MovieContainer> loader) {
            }
        });
    }

    private void initWatchlistLoader() {
        getLoaderManager().initLoader(WATCHLIST_LOADER_ID, getArguments(), new LoaderManager.LoaderCallbacks<MovieContainer>() {
            @Override
            public Loader<MovieContainer> onCreateLoader(int id, Bundle args) {
                Dao<WatchlistTable, Long> watchlistDao = OpenHelperManager.getHelper(getContext(), MovieDatabaseHelper.class).getWatchlistDao();
                return new WatchlistLoader(getContext(), watchlistDao);
            }

            @Override
            public void onLoadFinished(Loader<MovieContainer> loader, MovieContainer data) {
                if (data != null) {
                    if (data.getMovies().size() != 0) {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        watchlistRecyclerView.setAdapter(new MyListsPreviewAdapter(data.getMovies(), sessionId));
                        watchlistRecyclerView.setLayoutManager(layoutManager);
                        setContentVisibility(true, watchlistRecyclerView, watchlistMoreText, emptyWatchlistText);
                    } else {
                        setContentVisibility(false, watchlistRecyclerView, watchlistMoreText, emptyWatchlistText);
                    }
                }
                watchlistProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoaderReset(Loader<MovieContainer> loader) {
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void setContentVisibility(boolean visible, RecyclerView list, TextView moreButton, TextView emptyListText) {
        if (visible) {
            list.setVisibility(View.VISIBLE);
            moreButton.setVisibility(View.VISIBLE);
            emptyListText.setVisibility(View.GONE);
        } else {
            list.setVisibility(View.GONE);
            moreButton.setVisibility(View.GONE);
            emptyListText.setVisibility(View.VISIBLE);
        }
    }
}
