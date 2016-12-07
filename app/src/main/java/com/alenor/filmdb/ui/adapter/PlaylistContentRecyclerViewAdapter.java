package com.alenor.filmdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.alenor.filmdb.model.Movie;

import java.util.List;

public class PlaylistContentRecyclerViewAdapter extends UserMovieListRecyclerViewAdapter {

    public interface OnItemRemovedListener {
        void onItemRemoved(int position, Movie movie);
    }

    private final ItemTouchHelper.SimpleCallback playlistTouchHelperCallbacks =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    if (direction == ItemTouchHelper.RIGHT) {
                        int position = viewHolder.getAdapterPosition();
                        listener.onItemRemoved(position, movies.get(position));
                        movies.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            };

    private List<Movie> movies;
    private OnItemRemovedListener listener;

    public PlaylistContentRecyclerViewAdapter(List<Movie> movies, OnItemRemovedListener listener) {
        super(movies);
        this.movies = movies;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    public void attachTouchHelperToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(playlistTouchHelperCallbacks);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void insertItem(int position, Movie movie) {
        movies.add(position, movie);
        notifyItemInserted(position);
    }
}
