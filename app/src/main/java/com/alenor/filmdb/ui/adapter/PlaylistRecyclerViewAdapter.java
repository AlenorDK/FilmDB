package com.alenor.filmdb.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.Playlist;
import com.alenor.filmdb.model.PlaylistContainer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PlaylistRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Callbacks {
        void onPlaylistItemClick(String playlistId);
    }

    private PlaylistContainer playlistContainer;
    private Callbacks listener;

    public PlaylistRecyclerViewAdapter(PlaylistContainer playlistContainer, Callbacks listener) {
        this.playlistContainer = playlistContainer;
        this.listener = listener;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_recycler_view_adapter_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Playlist currentItem = playlistContainer.getResults().get(position);
        final ImageView playlistPoster = (ImageView) holder.itemView.findViewById(R.id.playlist_recycler_view_adapter_list_item_playlist_poster);
        final String posterPath = String.format("%1$s%2$s", MovieDBService.IMAGE_780W_BASE_URL,
                currentItem.getPosterPath());
        if (currentItem.getPosterPath() != null) {
            Picasso.with(holder.itemView.getContext()).load(posterPath).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    Picasso.with(holder.itemView.getContext()).load(posterPath).into(playlistPoster);
                }

                @Override
                public void onError() {

                }
            });
        } else {
            playlistPoster.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.no_poster));
        }
        TextView listName = (TextView) holder.itemView.findViewById(R.id.playlist_recycler_view_adapter_list_item_playlist_name_label);
        TextView movieCount = (TextView) holder.itemView.findViewById(R.id.playlist_recycler_view_adapter_list_item_movie_count_label);
        listName.setText(currentItem.getName());
        int count = currentItem.getItemCount();
        if (count == 1) {
            String movieCountText = "%1$d movie";
            movieCount.setText(String.format(
                    movieCountText, count
            ));
        } else {
            String movieCountText = "%1$d movies";
            movieCount.setText(String.format(
                    movieCountText, count
            ));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlaylistItemClick(currentItem.getPlaylistId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistContainer.getResults().size();
    }

}
