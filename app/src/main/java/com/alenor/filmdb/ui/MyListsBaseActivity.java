package com.alenor.filmdb.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alenor.filmdb.R;
import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.adapter.UserMovieListRecyclerViewAdapter;

abstract public class MyListsBaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieContainer> {

    private RecyclerView moviesRecyclerView;
    private UserMovieListRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;

    @StringRes
    abstract protected int titleResId();

    abstract protected int loaderId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_lists_base_activity);

        moviesRecyclerView = (RecyclerView) findViewById(R.id.my_lists_base_activity_layout_movie_list);
        GridLayoutManager layoutManager = new GridLayoutManager(MyListsBaseActivity.this, 2);
        moviesRecyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_lists_base_activity_layout_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(titleResId());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.my_lists_base_activity_layout_progress_bar);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.my_lists_base_activity_layout_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initLoader();
                refreshLayout.setRefreshing(false);
            }
        });
        initLoader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_lists_base_activity_toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.my_lists_base_activity_toolbar_menu_action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.my_lists_base_activity_toolbar_action_sort) {
            createSortDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initLoader() {
        getSupportLoaderManager().initLoader(loaderId(), null, this);
    }

    public void setAdapter(UserMovieListRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        moviesRecyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
    }

    private void createSortDialog() {
        final String EXTRA_MODE = "extra_mode";
        final String EXTRA_ASCENDING = "extra_ascending";

        final MaterialDialog dialog = new MaterialDialog.Builder(this).title(R.string.movie_sort_dialog_sort_by_text)
                .customView(R.layout.movie_sort_dialog_layout, false)
                .positiveText("OK").build();

        View view = dialog.getCustomView();

        final RadioGroup typeRadioGroup = (RadioGroup) view.findViewById(R.id.movie_sort_dialog_layout_type_radio_group);
        final RadioGroup methodRadioGroup = (RadioGroup) view.findViewById(R.id.movie_sort_dialog_layout_method_radio_group);

        RadioButton sortByTitleRadioButton = (RadioButton) view.findViewById(R.id.movie_sort_dialog_layout_sort_by_title_radio_button);
        RadioButton sortByRatingRadioButton = (RadioButton) view.findViewById(R.id.movie_sort_dialog_layout_sort_by_rating_radio_button);
        RadioButton sortAscendingRadioButton = (RadioButton) view.findViewById(R.id.movie_sort_dialog_layout_sort_ascending_button);
        RadioButton sortDescendingRadioButton = (RadioButton) view.findViewById(R.id.movie_sort_dialog_layout_sort_descending_radio_button);

        SharedPreferences session = SharedPrefUtils.getPreferences(this);
        int mode = session.getInt(EXTRA_MODE, 0);
        if (mode == 0) {
            sortByTitleRadioButton.setChecked(true);
        } else {
            sortByRatingRadioButton.setChecked(true);
        }
        boolean ascending = session.getBoolean(EXTRA_ASCENDING, true);
        if (ascending) {
            sortAscendingRadioButton.setChecked(true);
        } else {
            sortDescendingRadioButton.setChecked(true);
        }

        TextView okButton = dialog.getActionButton(DialogAction.POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mode = 0;
                boolean ascending = true;
                switch (typeRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.movie_sort_dialog_layout_sort_by_title_radio_button:
                        mode = 0;
                        break;
                    case R.id.movie_sort_dialog_layout_sort_by_rating_radio_button:
                        mode = 1;
                        break;
                }
                switch (methodRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.movie_sort_dialog_layout_sort_ascending_button:
                        ascending = true;
                        break;
                    case R.id.movie_sort_dialog_layout_sort_descending_radio_button:
                        ascending = false;
                        break;
                }
                adapter.sort(mode, ascending);
                SharedPreferences.Editor editor = SharedPrefUtils.edit(MyListsBaseActivity.this);
                editor.putInt(EXTRA_MODE, mode);
                editor.putBoolean(EXTRA_ASCENDING, ascending);
                editor.apply();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onLoadFinished(Loader<MovieContainer> loader, MovieContainer data) {
        if (data != null) {
            UserMovieListRecyclerViewAdapter adapter = new UserMovieListRecyclerViewAdapter(data.getMovies());
            setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieContainer> loader) {
        //do nothing
    }
}
