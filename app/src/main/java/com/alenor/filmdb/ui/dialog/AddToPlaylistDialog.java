package com.alenor.filmdb.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alenor.filmdb.R;
import com.alenor.filmdb.database.MovieDatabaseHelper;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.PlaylistTable;
import com.alenor.filmdb.model.ChangePlaylistBody;
import com.alenor.filmdb.model.CreatePlaylistBody;
import com.alenor.filmdb.model.CreatePlaylistResponse;
import com.alenor.filmdb.model.PlaylistContainer;
import com.alenor.filmdb.model.StatusResponse;
import com.alenor.filmdb.ui.MovieActivity;
import com.alenor.filmdb.ui.PlaylistFragment;
import com.alenor.filmdb.ui.adapter.PlaylistRecyclerViewAdapter;
import com.alenor.filmdb.ui.loader.AddMovieToPlaylistLoader;
import com.alenor.filmdb.ui.loader.CreatePlaylistLoader;
import com.alenor.filmdb.ui.loader.PlaylistLoader;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class AddToPlaylistDialog extends DialogFragment {

    private static final int PLAYLIST_LOADER_ID = 0;
    private static final int CHANGE_PLAYLIST_LOADER_ID = 2;
    private static final int CREATE_LIST_LOADER_ID = 3;

    public static AddToPlaylistDialog newInstance(Bundle bundle) {
        AddToPlaylistDialog fragment = new AddToPlaylistDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    private RecyclerView playlistsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_to_playlist_dialog_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        playlistsRecyclerView = (RecyclerView) view.findViewById(R.id.add_to_playlist_dialog_layout_playlists);
        Button newPlaylistButton = (Button) view.findViewById(R.id.add_to_playlist_dialog_layout_new_playlist_button);
        newPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePlaylistDialog dialog = CreatePlaylistDialog.newInstance(new CreatePlaylistDialog.OnPlaylistCreatedListener() {
                    @Override
                    public void onPlaylistCreated(String playlistName, String playlistDescription) {
                        createNewList(playlistName, playlistDescription);
                    }
                });
                dialog.show(getFragmentManager(), "Create playlist");
            }
        });
        loadPlaylists();
    }

    private void loadPlaylists() {
        getLoaderManager().initLoader(PLAYLIST_LOADER_ID, null, new LoaderManager.LoaderCallbacks<PlaylistContainer>() {
            @Override
            public Loader<PlaylistContainer> onCreateLoader(int id, Bundle args) {
                Dao<PlaylistTable, String> playlistDao = OpenHelperManager.getHelper(getContext(), MovieDatabaseHelper.class).getPlaylistDao();
                return new PlaylistLoader(getContext(), playlistDao);
            }

            @Override
            public void onLoadFinished(Loader<PlaylistContainer> loader, PlaylistContainer data) {
                RecyclerView.Adapter adapter = new PlaylistRecyclerViewAdapter(data, new PlaylistRecyclerViewAdapter.Callbacks() {
                    @Override
                    public void onPlaylistItemClick(String playlistId) {
                        addToPlaylist(playlistId);
                    }
                });
                playlistsRecyclerView.setAdapter(adapter);
                playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onLoaderReset(Loader<PlaylistContainer> loader) {
            }
        });
    }

    private void addToPlaylist(String playlistId) {
        Bundle bundle = new Bundle();
        bundle.putAll(getArguments());
        bundle.putString(PlaylistFragment.EXTRA_PLAYLIST_ID, playlistId);
        getLoaderManager().restartLoader(CHANGE_PLAYLIST_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<StatusResponse>() {
            @Override
            public Loader<StatusResponse> onCreateLoader(int id, Bundle args) {
                ChangePlaylistBody body = new ChangePlaylistBody(args.getLong(MovieActivity.EXTRA_MOVIE_ID));
                return new AddMovieToPlaylistLoader(getContext(), args, body);
            }

            @Override
            public void onLoadFinished(Loader<StatusResponse> loader, StatusResponse data) {
                if (data != null) {
                    if (data.getStatusCode() == StatusResponse.STATUS_UPDATED) {
                        Snackbar.make(getActivity().findViewById(R.id.movie_activity_content_layout),
                                "Movie is added to playlist", Snackbar.LENGTH_SHORT).show();
                        dismissAllowingStateLoss();
                    }
                    if (data.getStatusCode() == StatusResponse.STATUS_DUPLICATED) {
                        Snackbar.make(playlistsRecyclerView,
                                "This movie already exists in selected playlist", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    dismissAllowingStateLoss();
                }
            }

            @Override
            public void onLoaderReset(Loader<StatusResponse> loader) {

            }
        });
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
                        Snackbar.make(playlistsRecyclerView, "Playlist is created", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<CreatePlaylistResponse> loader) {

            }
        });
    }
}
