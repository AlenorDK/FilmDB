package com.alenor.filmdb.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.ui.MovieActivity;

import java.util.ArrayList;
import java.util.List;

public class UserMovieListRecyclerViewAdapter extends BaseMovieListAdapter {

    private List<Movie> movies;

    public UserMovieListRecyclerViewAdapter(List<Movie> movies) {
        super(movies);
        this.movies = movies;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Context context = holder.itemView.getContext();
        final Movie movie = movies.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sessionId = SharedPrefUtils.getSessionId(holder.itemView.getContext());
                MovieActivity.startAsUser(context, movie.getId(), sessionId);
            }
        });
    }
}
