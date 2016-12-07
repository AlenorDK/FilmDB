package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class CreatePlaylistResponse {

    public static final int STATUS_CREATED = 1;

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("status_message")
    private String statusMessage;

    @SerializedName("list_id")
    private String listId;

    public CreatePlaylistResponse(int statusCode, String statusMessage, String listId) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.listId = listId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }
}
