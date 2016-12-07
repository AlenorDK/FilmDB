package com.alenor.filmdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.Movie;
import com.alenor.filmdb.ui.FavoriteMoviesActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BaseMovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SORT_BY_TITLE = 0;
    private static final int SORT_BY_RATING = 1;

    private List<Movie> movies;
    private List<Movie> originalMovies;
    private Filter filter;

    public BaseMovieListAdapter(List<Movie> movies) {
        if (movies == null) {
            this.movies = new ArrayList<>(0);
        } else {
            this.movies = movies;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.base_movie_list_adapter_movie_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView posterImage = (ImageView) holder.itemView.findViewById(R.id.top_rated_adapter_poster_image);
        Movie movie = movies.get(position);
        String posterPath = String.format("%1$s%2$s", MovieDBService.IMAGE_780W_BASE_URL,
                movie.getPosterPath());
        Picasso.with(holder.itemView.getContext())
                .load(posterPath).placeholder(R.drawable.image_loading_placeholder)
                .into(posterImage);
        TextView title = (TextView) holder.itemView.findViewById(R.id.top_rated_adapter_title_label);
        title.setText(movie.getTitle());
        TextView vote = (TextView) holder.itemView.findViewById(R.id.top_rated_adapter_vote_label);
        vote.setText(String.format(
                holder.itemView.getContext().getString(R.string.votes_text),
                movie.getVoteAverage(),
                movie.getVoteCount()
        ));
        String description = movie.getOverview();
        if (!TextUtils.equals(description, "") && description != null) {
            TextView movieDescription = (TextView) holder.itemView.findViewById(R.id.top_rated_adapter_movie_description_label);
            movieDescription.setText(description);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new MovieListFilter();
        }
        return filter;
    }

    public void sort(int mode, boolean ascending) {

        Comparator<Movie> comparator = null;

        if (mode == SORT_BY_TITLE) {
            comparator = new Comparator<Movie>() {
                @Override
                public int compare(Movie lhs, Movie rhs) {
                    String leftTitle = lhs.getTitle().toLowerCase();
                    String rightTitle = rhs.getTitle().toLowerCase();
                    return leftTitle.compareTo(rightTitle);
                }
            };
        }
        if (mode == SORT_BY_RATING) {
            comparator = new Comparator<Movie>() {
                @Override
                public int compare(Movie lhs, Movie rhs) {
                    Double leftRating = lhs.getVoteAverage();
                    Double rightRating = rhs.getVoteAverage();
                    return leftRating.compareTo(rightRating);
                }
            };
        }

        if (originalMovies != null) {
            Collections.sort(originalMovies, comparator);
            if (!ascending) {
                Collections.reverse(originalMovies);
            }
        } else {
            Collections.sort(movies, comparator);
            if (!ascending) {
                Collections.reverse(movies);
            }
        }

        notifyDataSetChanged();
    }

    private class MovieListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (originalMovies == null) {
                originalMovies = new ArrayList<>(movies);
            }

            if (TextUtils.equals(constraint, "") || constraint == null) {
                ArrayList<Movie> list = new ArrayList<>(originalMovies);
                filterResults.values = list;
                filterResults.count = list.size();
            } else {
                final List<Movie> result = new ArrayList<>();
                for (int i = 0; i < originalMovies.size(); i++) {
                    Movie movie = originalMovies.get(i);
                    String title = movie.getTitle().toLowerCase();
                    String prefix = constraint.toString().toLowerCase();
                    if (title.contains(prefix)) {
                        result.add(movie);
                    }
                }
                filterResults.values = result;
                filterResults.count = result.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            movies = (List<Movie>) results.values;
            notifyDataSetChanged();
        }
    }
}
