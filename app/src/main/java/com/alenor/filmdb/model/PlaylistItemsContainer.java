package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistItemsContainer {

    @SerializedName("created_by")
    String createdBy;

    String description;
    String id;

    List<Movie> items;

    @SerializedName("item_count")
    int itemCount;

    String name;

    public PlaylistItemsContainer(String createdBy, String description, String id, List<Movie> items, int itemCount, String name) {
        this.createdBy = createdBy;
        this.description = description;
        this.id = id;
        this.items = items;
        this.itemCount = itemCount;
        this.name = name;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public List<Movie> getItems() {
        return items;
    }
}
