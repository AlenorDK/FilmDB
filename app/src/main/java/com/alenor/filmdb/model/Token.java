package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("request_token")
    private String requestToken;

    @SerializedName("success")
    private boolean isSuccessful;

    public Token(String requestToken, boolean isSuccessful) {
        this.requestToken = requestToken;
        this.isSuccessful = isSuccessful;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
