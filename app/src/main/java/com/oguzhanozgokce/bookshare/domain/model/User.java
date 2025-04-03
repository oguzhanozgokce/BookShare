package com.oguzhanozgokce.bookshare.domain.model;

import java.util.Map;

public class User {
    private String name;
    private String email;
    private Map<String, Listing> listings;

    public User() {}

    public User(String name, String email, Map<String, Listing> listings) {
        this.name = name;
        this.email = email;
        this.listings = listings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Listing> getListings() {
        return listings;
    }

    public void setListings(Map<String, Listing> listings) {
        this.listings = listings;
    }
}
