package com.alenor.filmdb.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alenor.filmdb.ui.GenreFragment;
import com.alenor.filmdb.ui.GuestMenuActivity;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.ui.MovieSearchFragment;
import com.alenor.filmdb.ui.TopRatedFragment;

import java.util.ArrayList;
import java.util.List;

public class GuestMenuActivityPagerAdapter extends FragmentPagerAdapter {

    private static final String MOVIE_SEARCH_FRAGMENT_TITLE = "Movie search";
    private static final String TOP_RATED_FRAGMENT_TITLE = "Top rated";
    private final List<String> fragmentTitles = new ArrayList<>(GuestMenuActivity.PAGE_COUNT);

    public GuestMenuActivityPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentTitles.add(MOVIE_SEARCH_FRAGMENT_TITLE);
        fragmentTitles.add(TOP_RATED_FRAGMENT_TITLE);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MovieSearchFragment.newInstance();
            case 1:
                return TopRatedFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return GuestMenuActivity.PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }
}