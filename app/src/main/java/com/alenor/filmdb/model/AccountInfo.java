package com.alenor.filmdb.model;

import com.google.gson.annotations.SerializedName;

public class AccountInfo {

    @SerializedName("avatar")
    private Avatar avatarHash;

    @SerializedName("id")
    private long accountId;

    @SerializedName("iso_639_1")
    String country;

    @SerializedName("name")
    String name;

    @SerializedName("include_adult")
    boolean adultIncluded;

    String username;

    public AccountInfo(Avatar avatarHash, long accountId, String country, String name, boolean adultIncluded,
                       String username) {
        this.avatarHash = avatarHash;
        this.accountId = accountId;
        this.country = country;
        this.name = name;
        this.adultIncluded = adultIncluded;
        this.username = username;
    }

    public String getAvatarHash() {
        return avatarHash.getGravatar().getHash();
    }

    public long getAccountId() {
        return accountId;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public boolean isAdultIncluded() {
        return adultIncluded;
    }

    public String getUsername() {
        return username;
    }

    private static class Avatar {
        private Gravatar gravatar;

        public Avatar(Gravatar gravatar) {
            this.gravatar = gravatar;
        }

        public Gravatar getGravatar() {
            return gravatar;
        }
    }

    private static class Gravatar {
        private String hash;

        public Gravatar(String hash) {
            this.hash = hash;
        }

        public String getHash() {
            return hash;
        }
    }
}
