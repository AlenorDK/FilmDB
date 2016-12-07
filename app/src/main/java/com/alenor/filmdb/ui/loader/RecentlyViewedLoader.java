package com.alenor.filmdb.ui.loader;

import android.content.Context;

import com.alenor.filmdb.database.table.RecentlyViewedTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class RecentlyViewedLoader extends BaseLoader<List<RecentlyViewedTable>> implements Dao.DaoObserver {

    private Dao<RecentlyViewedTable, Long> recentlyViewedDao;

    public RecentlyViewedLoader(Context context, Dao<RecentlyViewedTable, Long> recentlyViewedDao) {
        super(context);
        if (recentlyViewedDao == null) {
            throw new IllegalStateException("Dao is not initialized");
        } else {
            this.recentlyViewedDao = recentlyViewedDao;
        }
    }

    @Override
    public List<RecentlyViewedTable> loadInBackground() {
        QueryBuilder<RecentlyViewedTable, Long> query = recentlyViewedDao.queryBuilder();
        query.selectColumns("movieTitle", "moviePoster").orderBy("viewedDate", false);
        try {
            PreparedQuery<RecentlyViewedTable> preparedQuery = query.prepare();
            return recentlyViewedDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        recentlyViewedDao.registerObserver(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        recentlyViewedDao.unregisterObserver(this);
    }

    @Override
    public void onChange() {
        onContentChanged();
    }
}
