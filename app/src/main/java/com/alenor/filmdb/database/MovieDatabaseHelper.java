package com.alenor.filmdb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.alenor.filmdb.database.table.FavoritesTable;
import com.alenor.filmdb.database.table.PlaylistTable;
import com.alenor.filmdb.database.table.RecentlyViewedTable;
import com.alenor.filmdb.database.table.WatchlistTable;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class MovieDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "movieInfoDatabase";
    private static final int DATABASE_VERSION = 1;

    private Dao<FavoritesTable, Long> favoritesDao;
    private Dao<WatchlistTable, Long> watchlistDao;
    private Dao<PlaylistTable, String> playlistDao;
    private Dao<RecentlyViewedTable, Long> recentlyViewedDao;

    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, FavoritesTable.class);
            TableUtils.createTable(connectionSource, WatchlistTable.class);
            TableUtils.createTable(connectionSource, PlaylistTable.class);
            TableUtils.createTable(connectionSource, RecentlyViewedTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        favoritesDao = getFavoritesDao();
        watchlistDao = getWatchlistDao();
        playlistDao = getPlaylistDao();
        recentlyViewedDao = getRecentlyViewedDao();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, FavoritesTable.class, true);
            TableUtils.dropTable(connectionSource, WatchlistTable.class, true);
            TableUtils.dropTable(connectionSource, PlaylistTable.class, true);
            TableUtils.dropTable(connectionSource, RecentlyViewedTable.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
        favoritesDao = null;
        watchlistDao = null;
        playlistDao = null;
        recentlyViewedDao = null;
    }

    public Dao<FavoritesTable, Long> getFavoritesDao() {
        if (favoritesDao == null) {
            try {
                favoritesDao = getDao(FavoritesTable.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return favoritesDao;
    }

    public Dao<WatchlistTable, Long> getWatchlistDao() {
        if (watchlistDao == null) {
            try {
                watchlistDao = getDao(WatchlistTable.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return watchlistDao;
    }

    public Dao<PlaylistTable, String> getPlaylistDao() {
        if (playlistDao == null) {
            try {
                playlistDao = getDao(PlaylistTable.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return playlistDao;
    }

    public Dao<RecentlyViewedTable, Long> getRecentlyViewedDao() {
        if (recentlyViewedDao == null) {
            try {
                recentlyViewedDao = getDao(RecentlyViewedTable.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return recentlyViewedDao;
    }

}
