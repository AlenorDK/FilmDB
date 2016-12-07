package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.database.MovieDatabaseManager;
import com.alenor.filmdb.database.table.PlaylistTable;
import com.alenor.filmdb.model.AccountInfo;
import com.alenor.filmdb.model.Playlist;
import com.alenor.filmdb.model.PlaylistContainer;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import retrofit2.Response;

public class PlaylistLoader extends BaseLoader<PlaylistContainer> implements Dao.DaoObserver {

    private String sessionId;
    private Dao<PlaylistTable, String> playlistDao;

    public PlaylistLoader(Context context, Dao<PlaylistTable, String> playlistDao) {
        super(context);
        sessionId = SharedPrefUtils.getSessionId(getContext());
        if (playlistDao == null) {
            throw new IllegalStateException("Dao is not initialized");
        } else {
            this.playlistDao = playlistDao;
        }
    }

    @Override
    public PlaylistContainer loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<AccountInfo> accountIdResponse = movieDBService.getAccountInfo(sessionId).execute();
            long accountId = accountIdResponse.body().getAccountId();
            Response<PlaylistContainer> response = movieDBService.getPlaylists(accountId, sessionId).execute();
            PlaylistContainer playlistContainer = response.body();

            playlistDao.unregisterObserver(this);

            Dao<PlaylistTable, String> playlistDao = MovieDatabaseManager.getInstance(getContext()).getDbHelper().getPlaylistDao();
            for (Playlist playlist : playlistContainer.getResults()) {

                try {
                    PreparedQuery<PlaylistTable> query = playlistDao.queryBuilder()
                            .selectColumns(PlaylistTable.PLAYLIST_DESCRIPTION_COLUMN_NAME)
                            .where().idEq(playlist.getPlaylistId()).prepare();

                    List<PlaylistTable> result = playlistDao.query(query);
                    if (result.size() != 0) {
                        String description = result.get(0).getPlaylistDescription();
                        PlaylistTable row = new PlaylistTable();
                        row.setPlaylistId(playlist.getPlaylistId());
                        row.setPlaylistDescription(description);
                        row.setPlaylistName(playlist.getName());

                        playlistDao.createOrUpdate(row);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            playlistDao.registerObserver(this);
            return playlistContainer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    protected void onReset() {
        super.onReset();
        playlistDao.unregisterObserver(this);
    }

    @Override
    public void onChange() {
        onContentChanged();
    }
}
