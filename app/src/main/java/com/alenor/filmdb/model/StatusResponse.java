package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {

    public static final int STATUS_UPDATED = 12;
    public static final int STATUS_DUPLICATED = 8;

    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("status_message")
    private String statusMessage;

    public StatusResponse(int statusCode, String statusMessage) {
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
