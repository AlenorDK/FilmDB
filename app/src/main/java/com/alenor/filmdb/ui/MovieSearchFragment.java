package com.alenor.filmdb.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.RecentlyViewedTable;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.adapter.GuestMovieListRecyclerViewAdapter;
import com.alenor.filmdb.ui.adapter.RecentlyViewedRecyclerViewAdapter;
import com.alenor.filmdb.ui.adapter.UserMovieListRecyclerViewAdapter;
import com.alenor.filmdb.ui.loader.FoundMoviesLoader;
import com.alenor.filmdb.ui.loader.RecentlyViewedLoader;
import com.j256.ormlite.dao.Dao;

import java.util.List;

public class MovieSearchFragment extends Fragment {

    public static MovieSearchFragment newInstance() {
        return new MovieSearchFragment();
    }

    public static final String EXTRA_MOVIE_TITLE = "extra_movie_title";
    private static final String EXTRA_IS_SEARCHING = "extra_is_searching";
    private static final int FOUND_MOVIES_LOADER_ID = 0;
    private static final int RECENTLY_VIEWED_LOADER = 1;
    private static final String EXTRA_CURRENT_QUERY = "extra_current_query";

    private RecyclerView foundMoviesList;
    private LinearLayout recentlyViewedLayout;
    private RecyclerView recentlyViewedList;
    private SearchView searchView;
    private RecyclerView.Adapter adapter;
    private TextView emptyRecentlyViewedText;
    private String sessionId;
    private boolean isSearching;
    private String currentQuery;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movie_search_fragment_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        foundMoviesList = (RecyclerView) view.findViewById(R.id.movie_search_fragment_found_movies_list);
        foundMoviesList.setHasFixedSize(true);
        recentlyViewedLayout = (LinearLayout) view.findViewById(R.id.movie_search_fragment_layout_recently_viewed_layout);
        recentlyViewedList = (RecyclerView) view.findViewById(R.id.movie_search_fragment_recently_viewed_list);
        recentlyViewedList.setHasFixedSize(true);
        emptyRecentlyViewedText = (TextView) view.findViewById(R.id.movie_search_fragment_layout_empty_list_label);
        sessionId = SharedPrefUtils.getSessionId(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadRecentlyViewed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EXTRA_IS_SEARCHING, isSearching);
        outState.putString(EXTRA_CURRENT_QUERY, currentQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            isSearching = savedInstanceState.getBoolean(EXTRA_IS_SEARCHING, false);
            if (isSearching) {
                currentQuery = savedInstanceState.getString(EXTRA_CURRENT_QUERY);
                restartLoader(currentQuery);
                recentlyViewedLayout.setVisibility(View.GONE);
                foundMoviesList.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchItem;
        if (sessionId != null) {
            inflater.inflate(R.menu.account_activity_toolbar_menu, menu);
            searchItem = menu.findItem(R.id.account_activity_toolbar_menu_action_search);
        } else {
            inflater.inflate(R.menu.guest_menu_activity_toolbar_menu, menu);
            searchItem = menu.findItem(R.id.guest_menu_activity_toolbar_menu_action_search);
        }
        searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setQueryHint("Enter movie title");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isOnline()) {
                    currentQuery = query;
                    restartLoader(query);
                } else {
                    Snackbar.make(foundMoviesList, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
                }
                hideKeyboardAfterSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isOnline()) {
                    currentQuery = newText;
                    restartLoader(newText);
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearching = false;
                recentlyViewedLayout.setVisibility(View.VISIBLE);
                foundMoviesList.setVisibility(View.GONE);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recentlyViewedLayout.setVisibility(View.GONE);
                foundMoviesList.setVisibility(View.VISIBLE);
                isSearching = true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.hasFocus() != hasFocus) {
                    recentlyViewedLayout.setVisibility(View.VISIBLE);
                    foundMoviesList.setVisibility(View.GONE);
                    hideKeyboardAfterSearch();
                }
            }
        });

        if (isSearching) {
            searchView.setIconified(false);
            searchView.setQuery(currentQuery, false);
        }
    }

    private void restartLoader(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MOVIE_TITLE, query);
        getLoaderManager().restartLoader(FOUND_MOVIES_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<MovieContainer>() {
            @Override
            public Loader<MovieContainer> onCreateLoader(int id, Bundle args) {
                return new FoundMoviesLoader(getActivity(), args);
            }

            @Override
            public void onLoadFinished(Loader<MovieContainer> loader, MovieContainer data) {
                if (data != null) {
                    if (sessionId == null) {
                        adapter = new GuestMovieListRecyclerViewAdapter(data);
                    } else {
                        adapter = new UserMovieListRecyclerViewAdapter(data.getMovies());
                    }
                    foundMoviesList.setAdapter(adapter);
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                    foundMoviesList.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void onLoaderReset(Loader<MovieContainer> loader) {

            }
        });
    }

    private void hideKeyboardAfterSearch() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void loadRecentlyViewed() {
        final Dao<RecentlyViewedTable, Long> recentlyViewedDao = MovieDatabaseManager.getInstance(getContext()).getDbHelper().getRecentlyViewedDao();
        getLoaderManager().initLoader(RECENTLY_VIEWED_LOADER, null, new LoaderManager.LoaderCallbacks<List<RecentlyViewedTable>>() {
            @Override
            public Loader<List<RecentlyViewedTable>> onCreateLoader(int id, Bundle args) {
                return new RecentlyViewedLoader(getContext(), recentlyViewedDao);
            }

            @Override
            public void onLoadFinished(Loader<List<RecentlyViewedTable>> loader, List<RecentlyViewedTable> data) {
                if (data.size() != 0) {
                    recentlyViewedList.setAdapter(new RecentlyViewedRecyclerViewAdapter(data, sessionId));
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
                    recentlyViewedList.setLayoutManager(layoutManager);
                    setRecentlyViewedVisibility(true);
                } else {
                    setRecentlyViewedVisibility(false);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<RecentlyViewedTable>> loader) {

            }
        });
    }

    private void setRecentlyViewedVisibility(boolean visible) {
        TextView recentlyViewedText =
                (TextView) getView().findViewById(R.id.movie_search_fragment_layout_recently_viewed_label);
        if (visible) {
            recentlyViewedList.setVisibility(View.VISIBLE);
            recentlyViewedText.setVisibility(View.VISIBLE);
            emptyRecentlyViewedText.setVisibility(View.GONE);
        } else {
            recentlyViewedList.setVisibility(View.GONE);
            recentlyViewedText.setVisibility(View.GONE);
            emptyRecentlyViewedText.setVisibility(View.VISIBLE);
        }
    }
}
