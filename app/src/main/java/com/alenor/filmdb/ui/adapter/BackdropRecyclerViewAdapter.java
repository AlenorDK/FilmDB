package com.alenor.filmdb.ui.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.MovieImages;
import com.alenor.filmdb.model.MovieImagesContainer;
import com.alenor.filmdb.ui.ImagePreviewPager;
import com.squareup.picasso.Picasso;
import com.alenor.filmdb.R;

import java.util.ArrayList;

public class BackdropRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MovieImagesContainer movieImagesContainer;
    private ArrayList<String> images;

    public BackdropRecyclerViewAdapter(MovieImagesContainer movieImagesContainer) {
        this.movieImagesContainer = movieImagesContainer;

        images = new ArrayList<>();
        images.add(String.format("%1$s%2$s",
                MovieDBService.IMAGE_780W_BASE_URL,
                movieImagesContainer.getPosters().get(0).getImagePath()
        ));
        for (MovieImages movieImages : movieImagesContainer.getBackdrops()) {
            String imagePath = String.format("%1$s%2$s",
                    MovieDBService.IMAGE_780W_BASE_URL, movieImages.getImagePath());
            images.add(imagePath);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.backdrop_adapter_backdrop_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final int imagePosition = position;
        ImageView backdropImage = (ImageView) holder.itemView;
        String backdropPath = String.format("%1$s%2$s",
                MovieDBService.IMAGE_780W_BASE_URL, movieImagesContainer.getBackdrops().get(position).getImagePath());
        Picasso.with(holder.itemView.getContext()).load(backdropPath)
                .placeholder(R.drawable.image_loading_placeholder)
                .into(backdropImage);
        backdropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreviewPager.start(holder.itemView.getContext(), images, imagePosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieImagesContainer.getBackdrops().size();
    }
}
