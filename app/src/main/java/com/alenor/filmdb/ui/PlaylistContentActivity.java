package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alenor.filmdb.R;
import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.database.MovieDatabaseHelper;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.PlaylistTable;
import com.alenor.filmdb.model.ChangePlaylistBody;
import com.alenor.filmdb.model.DeletePlaylistResponse;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.model.PlaylistItemsContainer;
import com.alenor.filmdb.model.StatusResponse;
import com.alenor.filmdb.ui.adapter.PlaylistContentRecyclerViewAdapter;
import com.alenor.filmdb.ui.dialog.PlaylistDeleteConfirmationDialog;
import com.alenor.filmdb.ui.loader.ChangeMovieFromPlaylistLoader;
import com.alenor.filmdb.ui.loader.DeletePlaylistLoader;
import com.alenor.filmdb.ui.loader.PlaylistItemsLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

public class PlaylistContentActivity extends AppCompatActivity {

    public static void start(Context context, String playlistId) {
        Intent i = new Intent(context, PlaylistContentActivity.class);
        i.putExtra(EXTRA_PLAYLIST_ID, playlistId);
        context.startActivity(i);
    }

    public static final int RESULT_MOVIE_REMOVED = 1;
    private static final int PLAYLIST_ITEMS_LOADER_ID = 0;
    private static final int CHANGE_PLAYLIST_LOADER_ID = 1;
    private static final int DELETE_PLAYLIST_LOADER_ID = 2;
    private static final String EXTRA_IS_EDITING = "extra_is_editing";
    private static final String EXTRA_NEW_DESCRIPTION = "extra_new_description";
    private static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";

    private RecyclerView playlist;
    private Toolbar toolbar;
    private EditText playlistDescription;
    private PlaylistContentRecyclerViewAdapter playlistAdapter;
    private DialogFragment dialog;
    private ProgressBar progressBar;
    private ImageView editDescriptionButton;
    private ImageView commitDescriptionChangesButton;
    private boolean isEditing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_content_activity_layout);

        toolbar = (Toolbar) findViewById(R.id.playlist_content_activity_layout_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        playlist = (RecyclerView) findViewById(R.id.playlist_content_activity_layout_playlist);
        commitDescriptionChangesButton = (ImageView) findViewById(R.id.playlist_content_activity_layout_description_text_commit_button);
        editDescriptionButton = (ImageView) findViewById(R.id.playlist_content_activity_layout_description_text_edit_button);
        playlistDescription = (EditText) findViewById(R.id.playlist_content_activity_layout_description_text);
        playlistDescription.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);

        progressBar = (ProgressBar) findViewById(R.id.playlist_content_activity_layout_progress_bar);


        editDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPlaylistDescription();
            }
        });


        commitDescriptionChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitDescriptionChanges();
            }
        });

        if (isOnline()) {
            loadPlaylist();
        } else {
            Snackbar.make(playlist, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EXTRA_IS_EDITING, isEditing);
        if (isEditing) {
            outState.putString(EXTRA_NEW_DESCRIPTION, playlistDescription.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            isEditing = savedInstanceState.getBoolean(EXTRA_IS_EDITING);
            if (isEditing) {
                playlistDescription.setText(savedInstanceState.getString(EXTRA_NEW_DESCRIPTION));
                editPlaylistDescription();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.playlist_content_activity_toolbar_menu_action_delete_playlist:
                showDeleteConfirmationDialog();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_content_activity_toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.playlist_content_activity_toolbar_menu_action_sort);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                playlistAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                playlistAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    private void loadPlaylist() {
        getSupportLoaderManager().restartLoader(PLAYLIST_ITEMS_LOADER_ID, null, new LoaderManager.LoaderCallbacks<PlaylistItemsContainer>() {
            @Override
            public Loader<PlaylistItemsContainer> onCreateLoader(int id, Bundle args) {
                return new PlaylistItemsLoader(PlaylistContentActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<PlaylistItemsContainer> loader, PlaylistItemsContainer data) {
                if (data != null) {
                    setResult(RESULT_MOVIE_REMOVED);
                    playlistAdapter = new PlaylistContentRecyclerViewAdapter(data.getItems(), new PlaylistContentRecyclerViewAdapter.OnItemRemovedListener() {
                        @Override
                        public void onItemRemoved(final int position, final Movie movie) {
                            removeMovie(movie);
                            Snackbar.make(playlist, "Movie is removed from playlist", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            playlistAdapter.insertItem(position, movie);
                                            addMovie(movie);
                                        }
                                    })
                                    .show();
                        }
                    });
                    playlistAdapter.attachTouchHelperToRecyclerView(playlist);
                    playlist.setAdapter(playlistAdapter);
                    playlist.setLayoutManager(new LinearLayoutManager(PlaylistContentActivity.this));
                    toolbar.setTitle(data.getName());

                    MovieDatabaseHelper dbHelper = MovieDatabaseManager.getInstance(PlaylistContentActivity.this).getDbHelper();
                    Dao<PlaylistTable, String> playlistDao = dbHelper.getPlaylistDao();
                    String description = null;
                    try {

                        PreparedQuery<PlaylistTable> query = playlistDao.queryBuilder()
                                .selectColumns(PlaylistTable.PLAYLIST_DESCRIPTION_COLUMN_NAME)
                                .where().idEq(data.getId()).prepare();
                        List<PlaylistTable> result = playlistDao.query(query);
                        if (result.size() != 0) {
                            description = result.get(0).getPlaylistDescription();
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if (description == null) {
                        if (!TextUtils.equals(data.getDescription(), "")) {
                            playlistDescription.setText(data.getDescription());
                            playlistDescription.setVisibility(View.VISIBLE);
                            editDescriptionButton.setVisibility(View.VISIBLE);

                            try {
                                UpdateBuilder<PlaylistTable, String> update = playlistDao.updateBuilder();
                                update.where().idEq(data.getId());
                                update.updateColumnValue(PlaylistTable.PLAYLIST_DESCRIPTION_COLUMN_NAME, data.getDescription())
                                        .update();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        playlistDescription.setVisibility(View.VISIBLE);
                        if (!isEditing) {
                            playlistDescription.setText(description);
                            editDescriptionButton.setVisibility(View.VISIBLE);
                        } else {
                            playlistDescription.requestFocus();
                            playlistDescription.setSelection(playlistDescription.getText().length());
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoaderReset(Loader<PlaylistItemsContainer> loader) {

            }
        });
    }

    private void removeMovie(final Movie movie) {
        Bundle bundle = getIntent().getExtras();
        final ChangePlaylistBody body = new ChangePlaylistBody(movie.getId());
        getSupportLoaderManager().restartLoader(CHANGE_PLAYLIST_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<StatusResponse>() {
            @Override
            public Loader<StatusResponse> onCreateLoader(int id, Bundle args) {
                return new ChangeMovieFromPlaylistLoader(PlaylistContentActivity.this, args, body, ChangePlaylistBody.ACTION_REMOVE);
            }

            @Override
            public void onLoadFinished(Loader<StatusResponse> loader, StatusResponse data) {
            }

            @Override
            public void onLoaderReset(Loader<StatusResponse> loader) {

            }
        });
    }

    private void addMovie(final Movie movie) {
        Bundle bundle = getIntent().getExtras();
        final ChangePlaylistBody body = new ChangePlaylistBody(movie.getId());
        getSupportLoaderManager().restartLoader(CHANGE_PLAYLIST_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<StatusResponse>() {
            @Override
            public Loader<StatusResponse> onCreateLoader(int id, Bundle args) {
                return new ChangeMovieFromPlaylistLoader(PlaylistContentActivity.this, args, body, ChangePlaylistBody.ACTION_ADD);
            }

            @Override
            public void onLoadFinished(Loader<StatusResponse> loader, StatusResponse data) {

            }

            @Override
            public void onLoaderReset(Loader<StatusResponse> loader) {

            }
        });
    }

    private void showDeleteConfirmationDialog() {
        dialog = PlaylistDeleteConfirmationDialog.newInstance(new PlaylistDeleteConfirmationDialog.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirmed) {
                if (isConfirmed) {
                    deletePlaylist();
                    dialog.dismiss();
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "PlaylistDeleteConfirmationDialog");
    }

    private void deletePlaylist() {
        final String playlistId = getIntent().getStringExtra(PlaylistFragment.EXTRA_PLAYLIST_ID);
        Bundle bundle = new Bundle();
        bundle.putAll(getIntent().getExtras());
        getSupportLoaderManager().restartLoader(DELETE_PLAYLIST_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<DeletePlaylistResponse>() {
            @Override
            public Loader<DeletePlaylistResponse> onCreateLoader(int id, Bundle args) {
                return new DeletePlaylistLoader(PlaylistContentActivity.this, args);
            }

            @Override
            public void onLoadFinished(Loader<DeletePlaylistResponse> loader, DeletePlaylistResponse data) {
                if (data != null) {
                    if (data.getStatusCode() == DeletePlaylistResponse.STATUS_REMOVED) {
                        Dao<PlaylistTable, String> playlistDao = MovieDatabaseManager.getInstance(PlaylistContentActivity.this).getDbHelper().getPlaylistDao();
                        try {
                            if (playlistDao.idExists(playlistId)) {
                                playlistDao.deleteById(playlistId);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Snackbar.make(playlist, "Playlist is removed", Snackbar.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<DeletePlaylistResponse> loader) {

            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void editPlaylistDescription() {
        isEditing = true;
        playlistDescription.setEnabled(true);
        editDescriptionButton.setVisibility(View.GONE);
        commitDescriptionChangesButton.setVisibility(View.VISIBLE);
        playlistDescription.requestFocus();
        playlistDescription.setSelection(playlistDescription.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(playlistDescription, InputMethodManager.SHOW_FORCED);
    }

    private void commitDescriptionChanges() {
        isEditing = false;
        String newDescription = playlistDescription.getText().toString();
        String playlistId = getIntent().getStringExtra(PlaylistFragment.EXTRA_PLAYLIST_ID);

        MovieDatabaseHelper dbHelper = MovieDatabaseManager.getInstance(PlaylistContentActivity.this).getDbHelper();
        Dao<PlaylistTable, String> playlistDao = dbHelper.getPlaylistDao();
        try {
            UpdateBuilder<PlaylistTable, String> update = playlistDao.updateBuilder();
            update.where().idEq(playlistId);
            update.updateColumnValue(PlaylistTable.PLAYLIST_DESCRIPTION_COLUMN_NAME, newDescription)
                    .update();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        playlistDescription.setEnabled(false);
        editDescriptionButton.setVisibility(View.VISIBLE);
        commitDescriptionChangesButton.setVisibility(View.GONE);
    }
}
