package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class MovieList {

    private String description;

    @SerializedName("id")
    private String listId;

    @SerializedName("item_count")
    private int itemCount;

    @SerializedName("list_type")
    private String listType;

    private String name;

    @SerializedName("poster_path")
    private String posterPath;

    public MovieList(String description, String listId, int itemCount, String listType, String name, String posterPath) {
        this.description = description;
        this.listId = listId;
        this.itemCount = itemCount;
        this.listType = listType;
        this.name = name;
        this.posterPath = posterPath;
    }

    public String getDescription() {
        return description;
    }

    public String getListId() {
        return listId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String getListType() {
        return listType;
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
