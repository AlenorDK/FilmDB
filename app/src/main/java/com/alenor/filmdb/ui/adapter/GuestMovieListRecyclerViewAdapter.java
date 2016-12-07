package com.alenor.filmdb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.model.MovieContainer;
import com.alenor.filmdb.ui.MovieActivity;

public class GuestMovieListRecyclerViewAdapter extends BaseMovieListAdapter {

    MovieContainer movieContainer;

    public GuestMovieListRecyclerViewAdapter(MovieContainer movieContainer) {
        super(movieContainer.getMovies());
        this.movieContainer = movieContainer;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Context context = holder.itemView.getContext();
        final Movie movie = movieContainer.getMovies().get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieActivity.startAsGuest(context, movie.getId());
            }
        });
    }
}
