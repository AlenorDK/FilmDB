package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alenor.filmdb.R;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.FavoritesTable;
import com.alenor.filmdb.database.table.RecentlyViewedTable;
import com.alenor.filmdb.database.table.WatchlistTable;
import com.alenor.filmdb.model.ChangeFavoritesBody;
import com.alenor.filmdb.model.ChangeFavoritesResponse;
import com.alenor.filmdb.model.ChangeWatchlistBody;
import com.alenor.filmdb.model.ChangeWatchlistResponse;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.model.MovieImages;
import com.alenor.filmdb.model.MovieImagesContainer;
import com.alenor.filmdb.ui.adapter.BackdropRecyclerViewAdapter;
import com.alenor.filmdb.ui.dialog.AddToPlaylistDialog;
import com.alenor.filmdb.ui.loader.BackdropsLoader;
import com.alenor.filmdb.ui.loader.ChangeFavoritesResponseLoader;
import com.alenor.filmdb.ui.loader.ChangeWatchlistResponseLoader;
import com.alenor.filmdb.ui.loader.MovieLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    private static final String EXTRA_IMAGES = "extra_images";
    private static final String EXTRA_IS_GUEST = "extra_is_guest";
    private static final String EXTRA_SESSION_ID = "extra_session_id";
    private static final String EXTRA_IS_MOVIE_FAVORITE = "extra_is_movie_favorite";
    private static final String EXTRA_IS_MOVIE_IN_WATCHLIST = "extra_is_movie_in_watchlist";
    private static final int MOVIE_LOADER_ID = 0;
    private static final int BACKDROPS_LOADER_ID = 1;
    private static final int CHANGE_FAVORITES_RESPONSE_LOADER_ID = 2;
    private static final int CHANGE_WATCHLIST_RESPONSE_LOADER_ID = 3;

    public static void startAsGuest(Context context, long id) {
        Intent i = new Intent(context, MovieActivity.class);
        i.putExtra(EXTRA_IS_GUEST, true);
        i.putExtra(EXTRA_MOVIE_ID, id);
        context.startActivity(i);
    }

    public static void startAsUser(Context context, long movieId, String sessionId) {
        Intent i = new Intent(context, MovieActivity.class);
        i.putExtra(EXTRA_IS_GUEST, false);
        i.putExtra(EXTRA_SESSION_ID, sessionId);
        i.putExtra(EXTRA_MOVIE_ID, movieId);

        context.startActivity(i);
    }

    private boolean isGuest;
    private boolean isMovieFavorite;
    private boolean isMovieInWatchlist;

    private Movie movie;
    private TextView movieTitle;
    private ImageView posterImage;
    private TextView movieOverview;
    private TextView movieVoteCount;
    private ProgressBar loadingBar;
    private FrameLayout contentLayout;
    private RecyclerView backdropRecyclerView;
    private RecyclerView.Adapter backdropsRecyclerAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);

        loadingBar = (ProgressBar) findViewById(R.id.movie_activity_loading_progress_bar);
        contentLayout = (FrameLayout) findViewById(R.id.movie_activity_content_layout);
        contentLayout.setVisibility(View.GONE);

        isGuest = getIntent().getBooleanExtra(EXTRA_IS_GUEST, true);

        toolbar = (Toolbar) findViewById(R.id.movie_activity_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle("");

        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.movie_activity_floating_button);
        if (floatingActionButton != null) {
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(MovieActivity.this, R.drawable.ic_browse_movie));
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (movie != null) {
                        String url = String.format("%1$s%2$s", MovieDBService.MOVIE_DB_URL, movie.getId());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                }
            });
        }

        final long movieID = getIntent().getLongExtra(EXTRA_MOVIE_ID, 0);
        Bundle bundle = new Bundle();
        bundle.putLong(MovieLoader.BUNDLE_MOVIE_ID, movieID);

        movieTitle = (TextView) findViewById(R.id.movie_activity_movie_title_label);
        movieOverview = (TextView) findViewById(R.id.movie_activity_overview_label);
        movieVoteCount = (TextView) findViewById(R.id.movie_activity_vote_count_label);

        posterImage = (ImageView) findViewById(R.id.movie_activity_poster_image);
        posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        backdropRecyclerView = (RecyclerView) findViewById(R.id.movie_activity_backdrops_list);
        backdropRecyclerView.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<Movie>() {
            @Override
            public Loader<Movie> onCreateLoader(int id, Bundle args) {
                return new MovieLoader(MovieActivity.this, args);
            }

            @Override
            public void onLoadFinished(Loader<Movie> loader, Movie data) {
                if (data == null) {
                    Toast.makeText(MovieActivity.this, R.string.no_network_connection_error_text, Toast.LENGTH_SHORT).show();
                } else {
                    movie = data;
                    toolbar.setTitle(movie.getTitle());
                    movieTitle.setText(data.getTitle());
                    movieVoteCount.setText(String.format(
                            getString(R.string.votes_text),
                            data.getVoteAverage(),
                            data.getVoteCount())
                    );
                    movieOverview.setText(data.getOverview());
                    if (data.getPosterPath() != null) {
                        final String url = String.format(getString(R.string.image_load_format),
                                MovieDBService.IMAGE_780W_BASE_URL,
                                data.getPosterPath());
                        Picasso.with(MovieActivity.this)
                                .load(url).fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                Picasso.with(MovieActivity.this).load(url).into(posterImage);
                                addToRecentlyViewed();
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    } else {
                        posterImage.setVisibility(View.GONE);
                    }
                    setOptionsMenuState(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<Movie> loader) {
            }
        });

        getSupportLoaderManager().initLoader(BACKDROPS_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<MovieImagesContainer>() {
            @Override
            public Loader<MovieImagesContainer> onCreateLoader(int id, Bundle args) {
                return new BackdropsLoader(MovieActivity.this, args);
            }

            @Override
            public void onLoadFinished(Loader<MovieImagesContainer> loader, MovieImagesContainer data) {
                if (data != null) {
                    if (data.getBackdrops().size() != 0) {
                        backdropsRecyclerAdapter = new BackdropRecyclerViewAdapter(data);
                        backdropRecyclerView.setAdapter(backdropsRecyclerAdapter);
                        backdropRecyclerView.setLayoutManager(new LinearLayoutManager(
                                MovieActivity.this, LinearLayoutManager.HORIZONTAL, false));

                    } else {
                        backdropRecyclerView.setVisibility(View.GONE);
                    }
                    contentLayout.setVisibility(View.VISIBLE);

                    final ArrayList<String> images = new ArrayList<>();
                    if (data.getPosters().size() != 0) {
                        images.add(String.format("%1$s%2$s",
                                MovieDBService.IMAGE_780W_BASE_URL,
                                data.getPosters().get(0).getImagePath()
                        ));
                    }

                    for (MovieImages movieImages : data.getBackdrops()) {
                        String imagePath = String.format("%1$s%2$s",
                                MovieDBService.IMAGE_780W_BASE_URL, movieImages.getImagePath());
                        images.add(imagePath);
                    }
                    posterImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImagePreviewPager.start(MovieActivity.this, images, null);
                        }
                    });
                }
                loadingBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoaderReset(Loader<MovieImagesContainer> loader) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EXTRA_IS_GUEST, isGuest);
        outState.putBoolean(EXTRA_IS_MOVIE_FAVORITE, isMovieFavorite);
        outState.putBoolean(EXTRA_IS_MOVIE_IN_WATCHLIST, isMovieInWatchlist);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isGuest = savedInstanceState.getBoolean(EXTRA_IS_GUEST);
        isMovieFavorite = savedInstanceState.getBoolean(EXTRA_IS_MOVIE_FAVORITE, false);
        isMovieInWatchlist = savedInstanceState.getBoolean(EXTRA_IS_MOVIE_IN_WATCHLIST, false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.movie_activity_toolbar_menu_add_to_favorites:
                addToFavorites();
                isMovieFavorite = true;
                invalidateOptionsMenu();
                return true;
            case R.id.movie_activity_toolbar_menu_remove_from_favorites:
                removeFromFavorites();
                isMovieFavorite = false;
                invalidateOptionsMenu();
                return true;
            case R.id.movie_activity_toolbar_menu_add_to_watchlist:
                addToWatchlist();
                isMovieInWatchlist = true;
                invalidateOptionsMenu();
                return true;
            case R.id.movie_activity_toolbar_menu_remove_from_watchlist:
                removeFromWatchlist();
                isMovieInWatchlist = false;
                invalidateOptionsMenu();
                return true;
            case R.id.movie_activity_toolbar_menu_add_to_playlist:
                addToPlaylist();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!getIntent().getBooleanExtra(EXTRA_IS_GUEST, true)) {
            getMenuInflater().inflate(R.menu.movie_activity_toolbar_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isGuest) {
            if (isMovieFavorite) {
                menu.findItem(R.id.movie_activity_toolbar_menu_add_to_favorites).setVisible(false);
                menu.findItem(R.id.movie_activity_toolbar_menu_remove_from_favorites).setVisible(true);
            } else {
                menu.findItem(R.id.movie_activity_toolbar_menu_add_to_favorites).setVisible(true);
                menu.findItem(R.id.movie_activity_toolbar_menu_remove_from_favorites).setVisible(false);
            }
            if (isMovieInWatchlist) {
                menu.findItem(R.id.movie_activity_toolbar_menu_add_to_watchlist).setVisible(false);
                menu.findItem(R.id.movie_activity_toolbar_menu_remove_from_watchlist).setVisible(true);
            } else {
                menu.findItem(R.id.movie_activity_toolbar_menu_add_to_watchlist).setVisible(true);
                menu.findItem(R.id.movie_activity_toolbar_menu_remove_from_watchlist).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void addToFavorites() {
        final ChangeFavoritesBody body = new ChangeFavoritesBody(ChangeFavoritesBody.MEDIA_TYPE_MOVIE,
                movie.getId(), ChangeFavoritesBody.ADD_TO_FAVORITES);
        final String sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        getSupportLoaderManager().restartLoader(CHANGE_FAVORITES_RESPONSE_LOADER_ID, null, new LoaderManager.LoaderCallbacks<ChangeFavoritesResponse>() {
            @Override
            public Loader<ChangeFavoritesResponse> onCreateLoader(int id, Bundle args) {
                return new ChangeFavoritesResponseLoader(MovieActivity.this, sessionId, body);
            }

            @Override
            public void onLoadFinished(Loader<ChangeFavoritesResponse> loader, ChangeFavoritesResponse data) {
                if (ChangeFavoritesResponse.STATUS_ADDED == data.getStatusCode()) {

                    Dao<FavoritesTable, Long> favoritesDao = MovieDatabaseManager.getInstance(MovieActivity.this).getDbHelper().getFavoritesDao();
                    FavoritesTable row = new FavoritesTable();
                    row.setMovieId(movie.getId());
                    row.setIsFavorite(true);
                    try {
                        if (favoritesDao.idExists(movie.getId())) {
                            favoritesDao.update(row);
                        } else {
                            favoritesDao.create(row);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(contentLayout,
                            R.string.movie_activity_add_to_favorites_text, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<ChangeFavoritesResponse> loader) {

            }
        });
    }

    private void removeFromFavorites() {
        final ChangeFavoritesBody body = new ChangeFavoritesBody(ChangeFavoritesBody.MEDIA_TYPE_MOVIE, movie.getId(), ChangeFavoritesBody.REMOVE_FROM_FAVORITES);
        final String sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        getSupportLoaderManager().restartLoader(CHANGE_FAVORITES_RESPONSE_LOADER_ID, null, new LoaderManager.LoaderCallbacks<ChangeFavoritesResponse>() {
            @Override
            public Loader<ChangeFavoritesResponse> onCreateLoader(int id, Bundle args) {
                return new ChangeFavoritesResponseLoader(MovieActivity.this, sessionId, body);
            }

            @Override
            public void onLoadFinished(Loader<ChangeFavoritesResponse> loader, ChangeFavoritesResponse data) {
                if (ChangeFavoritesResponse.STATUS_REMOVED == data.getStatusCode()) {

                    Dao<FavoritesTable, Long> favoritesDao = MovieDatabaseManager.getInstance(MovieActivity.this).getDbHelper().getFavoritesDao();
                    try {
                        if (favoritesDao.idExists(movie.getId())) {
                            favoritesDao.deleteById(movie.getId());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(contentLayout,
                            R.string.movie_activity_remove_from_favorites_text, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<ChangeFavoritesResponse> loader) {

            }
        });
    }

    private void addToWatchlist() {
        final ChangeWatchlistBody body = new ChangeWatchlistBody(ChangeWatchlistBody.MEDIA_TYPE_MOVIE,
                movie.getId(), ChangeWatchlistBody.ADD_TO_WATCHLIST);
        final String sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        getSupportLoaderManager().restartLoader(CHANGE_WATCHLIST_RESPONSE_LOADER_ID, null, new LoaderManager.LoaderCallbacks<ChangeWatchlistResponse>() {
            @Override
            public Loader<ChangeWatchlistResponse> onCreateLoader(int id, Bundle args) {
                return new ChangeWatchlistResponseLoader(MovieActivity.this, sessionId, body);
            }

            @Override
            public void onLoadFinished(Loader<ChangeWatchlistResponse> loader, ChangeWatchlistResponse data) {
                if (ChangeWatchlistResponse.STATUS_ADDED == data.getStatusCode()) {

                    Dao<WatchlistTable, Long> watchlistDao = MovieDatabaseManager.getInstance(MovieActivity.this).getDbHelper().getWatchlistDao();
                    WatchlistTable row = new WatchlistTable();
                    row.setMovieId(movie.getId());
                    row.setIsInWatchlist(true);
                    try {
                        if (watchlistDao.idExists(movie.getId())) {
                            watchlistDao.update(row);
                        } else {
                            watchlistDao.create(row);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(contentLayout,
                            R.string.movie_activity_add_to_watchlist_text, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<ChangeWatchlistResponse> loader) {

            }
        });
    }

    private void removeFromWatchlist() {
        final ChangeWatchlistBody body = new ChangeWatchlistBody(ChangeWatchlistBody.MEDIA_TYPE_MOVIE,
                movie.getId(), ChangeWatchlistBody.REMOVE_FROM_WATCHLIST);
        final String sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        getSupportLoaderManager().restartLoader(CHANGE_WATCHLIST_RESPONSE_LOADER_ID, null, new LoaderManager.LoaderCallbacks<ChangeWatchlistResponse>() {
            @Override
            public Loader<ChangeWatchlistResponse> onCreateLoader(int id, Bundle args) {
                return new ChangeWatchlistResponseLoader(MovieActivity.this, sessionId, body);
            }

            @Override
            public void onLoadFinished(Loader<ChangeWatchlistResponse> loader, ChangeWatchlistResponse data) {
                if (ChangeWatchlistResponse.STATUS_REMOVED == data.getStatusCode()) {

                    Dao<WatchlistTable, Long> watchlistDao = MovieDatabaseManager.getInstance(MovieActivity.this).getDbHelper().getWatchlistDao();
                    try {
                        if (watchlistDao.idExists(movie.getId())) {
                            watchlistDao.deleteById(movie.getId());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(contentLayout,
                            R.string.movie_activity_remove_from_watchlist_text, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoaderReset(Loader<ChangeWatchlistResponse> loader) {
            }
        });
    }

    private void addToPlaylist() {
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_MOVIE_ID, movie.getId());
        bundle.putString(EXTRA_SESSION_ID, getIntent().getStringExtra(EXTRA_SESSION_ID));
        AddToPlaylistDialog dialog = AddToPlaylistDialog.newInstance(bundle);
        dialog.show(getSupportFragmentManager(), "AddToPlaylist dialog");
    }

    private void addToRecentlyViewed() {
        Dao<RecentlyViewedTable, Long> recentlyViewedDao = MovieDatabaseManager.getInstance(MovieActivity.this).getDbHelper().getRecentlyViewedDao();
        try {

            RecentlyViewedTable row = new RecentlyViewedTable();

            row.setMovieId(movie.getId());
            row.setMovieTitle(movie.getTitle());
            row.setMoviePoster(movie.getPosterPath());
            row.setViewedDate(SystemClock.elapsedRealtime());

            if (recentlyViewedDao.idExists(movie.getId())) {
                recentlyViewedDao.update(row);
            } else {
                QueryBuilder<RecentlyViewedTable, Long> query = recentlyViewedDao.queryBuilder();
                query.selectColumns("movieId").orderBy("viewedDate", true);
                PreparedQuery<RecentlyViewedTable> preparedQuery = query.prepare();
                List<RecentlyViewedTable> result = recentlyViewedDao.query(preparedQuery);

                if (result.size() == RecentlyViewedTable.MAX_ITEM_COUNT) {
                    recentlyViewedDao.deleteById(result.get(0).getMovieId());
                }

                recentlyViewedDao.create(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setOptionsMenuState(Movie movie) {
        Dao<FavoritesTable, Long> favoritesDao = MovieDatabaseManager.getInstance(MovieActivity.this).getDbHelper().getFavoritesDao();
        QueryBuilder<FavoritesTable, Long> favoritesQueryBuilder = favoritesDao.queryBuilder();
        try {
            favoritesQueryBuilder.where().eq(FavoritesTable.MOVIE_ID_FIELD_NAME, movie.getId());
            PreparedQuery<FavoritesTable> favoritesPreparedQuery = favoritesQueryBuilder.prepare();
            List<FavoritesTable> favoritesResult = favoritesDao.query(favoritesPreparedQuery);
            if (favoritesResult.size() > 0) {
                isMovieFavorite = favoritesResult.get(0).getIsFavorite();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Dao<WatchlistTable, Long> watchlistDao = MovieDatabaseManager.getInstance(MovieActivity.this).getDbHelper().getWatchlistDao();
        QueryBuilder<WatchlistTable, Long> watchlistQueryBuilder = watchlistDao.queryBuilder();
        try {
            watchlistQueryBuilder.where().eq(WatchlistTable.MOVIE_ID_FIELD_NAME, movie.getId());
            PreparedQuery<WatchlistTable> watchlistPreparedQuery = watchlistQueryBuilder.prepare();
            List<WatchlistTable> watchlistResult = watchlistDao.query(watchlistPreparedQuery);
            if (watchlistResult.size() > 0) {
                isMovieInWatchlist = watchlistResult.get(0).getIsInWatchlist();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        invalidateOptionsMenu();
    }
}
