package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.model.AccountInfo;
import com.alenor.filmdb.ui.dialog.LogOutConfirmationDialog;
import com.alenor.filmdb.ui.loader.AccountInfoLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    public static void startActivity(Context context) {
        Intent i = new Intent(context, AccountActivity.class);
        context.startActivity(i);
    }

    private static final String MOVIE_SEARCH_FRAGMENT = "MovieSearchFragment";
    private static final String FAVORITE_MOVIE_FRAGMENT = "FavoriteMovieFragment";
    private static final String PLAYLIST_FRAGMENT = "PlaylistFragment";
    private static final String CURRENT_FRAGMENT_EXTRA = "current_fragment_extra";
    private static final int ACCOUNT_INFO_LOADER_ID = 0;

    private String currentFragmentTag;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private LogOutConfirmationDialog logOutConfirmationDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        drawerLayout = (DrawerLayout) findViewById(R.id.account_activity_drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.account_activity_toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.account_activity_open_navi_drawer_text,
                R.string.account_activity_close_navi_drawer_text);
        drawerLayout.addDrawerListener(toggle);
        setFragment(FAVORITE_MOVIE_FRAGMENT);
        currentFragmentTag = FAVORITE_MOVIE_FRAGMENT;


        navigationView = (NavigationView) findViewById(R.id.account_activity_navigation_view);
        navigationView.setCheckedItem(R.id.account_activity_navigation_my_lists);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                toolbar.getMenu().clear();
                switch (item.getItemId()) {
                    case R.id.account_activity_navigation_search:
                        setFragment(MOVIE_SEARCH_FRAGMENT);
                        currentFragmentTag = MOVIE_SEARCH_FRAGMENT;
                        toolbar.inflateMenu(R.menu.account_activity_toolbar_menu);
                        return true;
                    case R.id.account_activity_navigation_my_lists:
                        setFragment(FAVORITE_MOVIE_FRAGMENT);
                        currentFragmentTag = FAVORITE_MOVIE_FRAGMENT;
                        return true;
                    case R.id.account_activity_navigation_my_playlists:
                        setFragment(PLAYLIST_FRAGMENT);
                        currentFragmentTag = PLAYLIST_FRAGMENT;
                        return true;
                    case R.id.account_activity_navigation_about:
                        AboutActivity.start(AccountActivity.this);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.account_activity_navigation_log_out:
                        logOut();
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
        if (TextUtils.equals(currentFragmentTag, MOVIE_SEARCH_FRAGMENT)) {
            toolbar.setTitle(R.string.account_activity_movie_search_title);
            navigationView.setCheckedItem(R.id.account_activity_navigation_search);
        }
        if (TextUtils.equals(currentFragmentTag, FAVORITE_MOVIE_FRAGMENT)) {
            toolbar.setTitle(R.string.account_activity_my_lists_title);
            navigationView.setCheckedItem(R.id.account_activity_navigation_my_lists);
        }
        if (TextUtils.equals(currentFragmentTag, PLAYLIST_FRAGMENT)) {
            toolbar.setTitle(R.string.account_activity_my_playlists_title);
            navigationView.setCheckedItem(R.id.account_activity_navigation_my_playlists);
        }
        String username = SharedPrefUtils.getUsername(this);
        navigationView.addHeaderView(createHeaderView(username));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_FRAGMENT_EXTRA, currentFragmentTag);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_EXTRA, FAVORITE_MOVIE_FRAGMENT);
        setFragment(currentFragmentTag);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void setFragment(String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    transaction.hide(fragment);
                }
            }
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            transaction.show(fragment);
            if (TextUtils.equals(tag, MOVIE_SEARCH_FRAGMENT)) {
                toolbar.setTitle(R.string.account_activity_movie_search_title);
            }
            if (TextUtils.equals(tag, FAVORITE_MOVIE_FRAGMENT)) {
                toolbar.setTitle(R.string.account_activity_my_lists_title);
            }
            if (TextUtils.equals(tag, PLAYLIST_FRAGMENT)) {
                toolbar.setTitle(R.string.account_activity_my_playlists_title);
            }
        } else {
            Fragment newFragment = null;
            if (TextUtils.equals(tag, MOVIE_SEARCH_FRAGMENT)) {
                newFragment = MovieSearchFragment.newInstance();
                toolbar.setTitle(R.string.account_activity_movie_search_title);
            }
            if (TextUtils.equals(tag, FAVORITE_MOVIE_FRAGMENT)) {
                newFragment = MyListsFragment.newInstance();
                toolbar.setTitle(R.string.account_activity_my_lists_title);
            }
            if (TextUtils.equals(tag, PLAYLIST_FRAGMENT)) {
                newFragment = PlaylistFragment.newInstance();
                toolbar.setTitle(R.string.account_activity_my_playlists_title);
            }
            transaction.add(R.id.account_activity_fragment_holder_layout, newFragment, tag);
        }
        transaction.commit();
        hideKeyboardAfterSearch();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void logOut() {
        logOutConfirmationDialog = LogOutConfirmationDialog.newInstance(new LogOutConfirmationDialog.OnConfirmListener() {
            @Override
            public void onConfirm(boolean isConfirmed) {
                if (isConfirmed) {
                    SharedPrefUtils.clear(AccountActivity.this);
                    logOutConfirmationDialog.dismiss();
                    LoginActivity.start(AccountActivity.this);
                }
            }
        });
        logOutConfirmationDialog.show(getSupportFragmentManager(), "LogOutConfirmationDialog");
    }

    private View createHeaderView(String username) {
        final View headerView = LayoutInflater.from(this).inflate(R.layout.account_activity_navigation_header_layout, navigationView, false);
        TextView tv = (TextView) headerView.findViewById(R.id.account_activity_navi_header_layout_username_label);
        tv.setText(username);

        SwitchCompat adultSwitch = (SwitchCompat) headerView.findViewById(R.id.account_activity_navi_header_layout_adult_switch);
        TextView adultSwitchLabel = (TextView) headerView.findViewById(R.id.account_activity_navi_header_layout_adult_switch_label);

        boolean isAdultIncluded = SharedPrefUtils.getAdult(this);
        if (isAdultIncluded) {
            adultSwitch.setChecked(true);
            adultSwitchLabel.setText(R.string.account_activity_navi_header_adult_included_text);
        } else {
            adultSwitch.setChecked(false);
            adultSwitchLabel.setText(R.string.account_activity_navi_header_adult_excluded_text);
        }

        adultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    SharedPrefUtils.setAdult(AccountActivity.this, true);
                    adultSwitchLabel.setText(R.string.account_activity_navi_header_adult_included_text);
                } else {
                    SharedPrefUtils.setAdult(AccountActivity.this, false);
                    adultSwitchLabel.setText(R.string.account_activity_navi_header_adult_excluded_text);
                }
            }
        });

        getSupportLoaderManager().initLoader(ACCOUNT_INFO_LOADER_ID, getIntent().getExtras(), new LoaderManager.LoaderCallbacks<AccountInfo>() {
            @Override
            public Loader<AccountInfo> onCreateLoader(int id, Bundle args) {
                return new AccountInfoLoader(AccountActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<AccountInfo> loader, AccountInfo data) {
                if (data != null) {
                    final CircleImageView iv = (CircleImageView) headerView.findViewById(R.id.account_activity_navi_header_layout_avatar);
                    final String avatarUrl = String.format("https://www.gravatar.com/avatar/%1$s?d=mm", data.getAvatarHash());
                    Picasso.with(AccountActivity.this).load(avatarUrl).fetch(new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.with(AccountActivity.this).load(avatarUrl).into(iv);
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }

            @Override
            public void onLoaderReset(Loader<AccountInfo> loader) {

            }
        });
        return headerView;
    }

    private void hideKeyboardAfterSearch() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drawerLayout.getWindowToken(), 0);
    }
}
