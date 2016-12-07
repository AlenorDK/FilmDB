package com.alenor.filmdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alenor.filmdb.R;
import com.alenor.filmdb.model.GenreContainer;

public class GenresRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface GenreAdapterOnClickListener {
        void onClick(long genreId);
    }

    private GenreContainer genreContainer;
    private GenreAdapterOnClickListener onClickListener;

    public GenresRecyclerViewAdapter(GenreContainer genreContainer, GenreAdapterOnClickListener onClickListener) {
        this.genreContainer = genreContainer;
        this.onClickListener = onClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genres_adapter_genre_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        TextView genre = (TextView) holder.itemView.findViewById(R.id.genres_adapter_genre_label);
        if (onClickListener != null) {
            final long genreId = genreContainer.getGenres().get(position).getId();
            genre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(genreId);
                }
            });
        }
        genre.setText(genreContainer.getGenres().get(position).getName());
    }

    @Override
    public int getItemCount() {
        return genreContainer.getGenres().size();
    }
}
