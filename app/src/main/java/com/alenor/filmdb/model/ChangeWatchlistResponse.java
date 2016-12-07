package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class ChangeWatchlistResponse {

    public static final int STATUS_ADDED = 1;
    public static final int STATUS_REMOVED = 13;

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("status_message")
    private String statusMessage;

    public ChangeWatchlistResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
