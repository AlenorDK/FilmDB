package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class MovieImages {

    @SerializedName("aspect_ratio")
    private double aspectRatio;

    @SerializedName("file_path")
    private String imagePath;

    private int height;
    private int width;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    public MovieImages(double aspectRatio, String imagePath, int height, int width, double voteAverage, int voteCount) {
        this.aspectRatio = aspectRatio;
        this.imagePath = imagePath;
        this.height = height;
        this.width = width;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }
}
