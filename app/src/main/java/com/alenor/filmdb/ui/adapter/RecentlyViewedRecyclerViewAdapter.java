package com.alenor.filmdb.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.database.table.RecentlyViewedTable;
import com.alenor.filmdb.ui.MovieActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecentlyViewedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RecentlyViewedTable> recentlyViewed;
    private String sessionId;

    public RecentlyViewedRecyclerViewAdapter(List<RecentlyViewedTable> recentlyViewed, String sessionId) {
        if (recentlyViewed == null) {
            this.recentlyViewed = new ArrayList<>(0);
        } else {
            this.recentlyViewed = recentlyViewed;
        }
        this.sessionId = sessionId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_search_recently_viewed_adapter_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView moviePoster = (ImageView) holder.itemView.findViewById(R.id.movie_search_recently_viewed_adapter_poster_image);
        TextView movieTitle = (TextView) holder.itemView.findViewById(R.id.movie_search_recently_viewed_adapter_title_label);

        final RecentlyViewedTable currentMovie = recentlyViewed.get(position);

        final String uri = String.format("%1$s%2$s",
                MovieDBService.IMAGE_780W_BASE_URL,
                currentMovie.getMoviePoster());
        Picasso.with(holder.itemView.getContext()).load(uri).into(moviePoster);
        movieTitle.setText(currentMovie.getMovieTitle());

        final Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionId != null) {
                    MovieActivity.startAsUser(context, currentMovie.getMovieId(), sessionId);
                } else {
                    MovieActivity.startAsGuest(context, currentMovie.getMovieId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentlyViewed.size();
    }
}
