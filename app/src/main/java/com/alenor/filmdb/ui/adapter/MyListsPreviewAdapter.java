package com.alenor.filmdb.ui.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.ui.MovieActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyListsPreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Movie> movies;
    private String sessionId;

    public MyListsPreviewAdapter(List<Movie> movies, String sessionId) {
        if (movies != null) {
            this.movies = movies;
        } else {
            this.movies = new ArrayList<>(0);
        }
        this.sessionId = sessionId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_lists_preview_adapter_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView posterImage = (ImageView) holder.itemView.findViewById(R.id.my_lists_preview_adapter_poster_image);
        TextView titleText = (TextView) holder.itemView.findViewById(R.id.my_lists_preview_adapter_title_label);
        final Movie movie = movies.get(position);
        String posterPath = String.format("%1$s%2$s", MovieDBService.IMAGE_780W_BASE_URL,
                movie.getPosterPath());
        Picasso.with(holder.itemView.getContext())
                .load(posterPath)
                .into(posterImage);
        titleText.setText(movie.getTitle());
        final Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieActivity.startAsUser(context, movie.getId(), sessionId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
