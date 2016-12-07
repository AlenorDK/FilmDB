package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.alenor.filmdb.R;
import com.alenor.filmdb.database.MovieDatabaseHelper;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.PlaylistTable;
import com.alenor.filmdb.model.CreatePlaylistBody;
import com.alenor.filmdb.model.CreatePlaylistResponse;
import com.alenor.filmdb.model.PlaylistContainer;
import com.alenor.filmdb.ui.adapter.PlaylistRecyclerViewAdapter;
import com.alenor.filmdb.ui.loader.CreatePlaylistLoader;
import com.alenor.filmdb.ui.loader.PlaylistLoader;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class PlaylistFragment extends Fragment {

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    public static final String EXTRA_PLAYLIST_ID = "extra_playlist_id";
    private static final int PLAYLIST_LOADER_ID = 0;
    private static final int CREATE_LIST_LOADER_ID = 2;

    private RecyclerView playlistRecyclerView;
    private TextView emptyPlaylistsText;
    private ProgressBar progressBar;
    private String newPlaylistName;
    private String newPlaylistDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.playlist_fragment_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistRecyclerView = (RecyclerView) view.findViewById(R.id.playlist_fragment_layout_list);
        emptyPlaylistsText = (TextView) view.findViewById(R.id.playlist_fragment_layout_empty_playlists_label);
        progressBar = (ProgressBar) view.findViewById(R.id.playlist_fragment_layout_progress_bar);
        FloatingActionButton actionButton = (FloatingActionButton)
                view.findViewById(R.id.playlist_fragment_layout_create_new_list_action_button);
        actionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_create_new_list));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    showCreateNewListDialog();
                } else {
                    Snackbar.make(playlistRecyclerView, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.playlist_fragment_layout_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    restartPlaylistLoader();
                } else {
                    Snackbar.make(playlistRecyclerView, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isOnline()) {
            restartPlaylistLoader();
        } else {
            Snackbar.make(playlistRecyclerView, R.string.no_network_connection_error_text, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PlaylistContentActivity.RESULT_MOVIE_REMOVED) {
            restartPlaylistLoader();
        }
    }

    private void restartPlaylistLoader() {
        getLoaderManager().restartLoader(PLAYLIST_LOADER_ID, null, new LoaderManager.LoaderCallbacks<PlaylistContainer>() {
                    @Override
                    public Loader<PlaylistContainer> onCreateLoader(int id, Bundle args) {
                        Dao<PlaylistTable, String> playlistDao = OpenHelperManager.getHelper(getContext(), MovieDatabaseHelper.class).getPlaylistDao();
                        return new PlaylistLoader(getContext(), playlistDao);
                    }

                    @Override
                    public void onLoadFinished(Loader<PlaylistContainer> loader, PlaylistContainer data) {
                        if (data != null) {
                            if (data.getResults().size() != 0) {
                                PlaylistRecyclerViewAdapter adapter = new PlaylistRecyclerViewAdapter(data, new PlaylistRecyclerViewAdapter.Callbacks() {
                                    @Override
                                    public void onPlaylistItemClick(String playlistId) {
                                        PlaylistContentActivity.start(getContext(), playlistId);
                                    }
                                });
                                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                                playlistRecyclerView.setAdapter(adapter);
                                playlistRecyclerView.setLayoutManager(layoutManager);
                                setContentVisibility(true);
                            } else {
                                setContentVisibility(false);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoaderReset(Loader<PlaylistContainer> loader) {

                    }
                }

        );
    }

    private void createNewList(String playlistName, String playlistDescription) {
        final CreatePlaylistBody body = new CreatePlaylistBody(playlistName, playlistDescription);
        getLoaderManager().restartLoader(CREATE_LIST_LOADER_ID, null, new LoaderManager.LoaderCallbacks<CreatePlaylistResponse>() {
            @Override
            public Loader<CreatePlaylistResponse> onCreateLoader(int id, Bundle args) {
                return new CreatePlaylistLoader(getContext(), body);
            }

            @Override
            public void onLoadFinished(Loader<CreatePlaylistResponse> loader, CreatePlaylistResponse data) {
                if (data != null) {
                    if (data.getStatusCode() == CreatePlaylistResponse.STATUS_CREATED) {
                        Dao<PlaylistTable, String> playlistDao = MovieDatabaseManager.getInstance(getContext()).getDbHelper().getPlaylistDao();
                        PlaylistTable row = new PlaylistTable();
                        row.setPlaylistId(data.getListId());
                        row.setPlaylistName(body.getName());
                        try {
                            playlistDao.createOrUpdate(row);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Snackbar.make(playlistRecyclerView, "Playlist is created", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<CreatePlaylistResponse> loader) {

            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void setContentVisibility(boolean visible) {
        if (visible) {
            playlistRecyclerView.setVisibility(View.VISIBLE);
            emptyPlaylistsText.setVisibility(View.GONE);
        } else {
            playlistRecyclerView.setVisibility(View.GONE);
            emptyPlaylistsText.setVisibility(View.VISIBLE);
        }
    }

    private void showCreateNewListDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("New playlist")
                .customView(R.layout.create_playlist_dialog_layout, false)
                .positiveText("OK")
                .negativeText("Cancel")
                .build();
        dialog.show();

        View dialogView = dialog.getCustomView();
        EditText nameText = (EditText) dialogView.findViewById(R.id.create_playlist_dialog_layout_playlist_name_text);
        EditText descriptionText = (EditText) dialogView.findViewById(R.id.create_playlist_dialog_layout_playlist_description_text);
        MDButton confirmButton = dialog.getActionButton(DialogAction.POSITIVE);
        MDButton negativeButton = dialog.getActionButton(DialogAction.NEGATIVE);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPlaylistName = nameText.getText().toString();
                newPlaylistDescription = descriptionText.getText().toString();
                if (!TextUtils.equals(newPlaylistName, "")) {
                    createNewList(newPlaylistName, newPlaylistDescription);
                    dialog.dismiss();
                } else {
                    Snackbar.make(dialogView, "You must enter playlist name", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
