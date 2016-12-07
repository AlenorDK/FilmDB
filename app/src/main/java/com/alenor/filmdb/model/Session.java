package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class Session {

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("success")
    private boolean isSuccessful;

    public Session(String additionalMessage, boolean isSuccessful, String sessionId) {
        this.sessionId = sessionId;
        this.isSuccessful = isSuccessful;
    }
    public String getSessionId() {
        return sessionId;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
