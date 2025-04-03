package com.oguzhanozgokce.bookshare.domain.model;

import com.oguzhanozgokce.bookshare.data.model.ListingType;

import java.io.Serializable;

public class Listing implements Serializable {
    private final String id;
    private final String title;
    private final String description;
    private final String condition;
    private final String location;
    private final ListingType type;
    private final int price;
    private final String address;
    private final boolean isShared;
    private final String startDate;
    private final String endDate;
    private final String createdAt;
    private final String updatedAt;

    public Listing(String id, String title, String description, String condition, String location,
                   ListingType type, int price, String address, boolean isShared,
                   String startDate, String endDate, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.location = location;
        this.type = type;
        this.price = price;
        this.address = address;
        this.isShared = isShared;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {return id;}
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCondition() { return condition; }
    public String getLocation() { return location; }
    public ListingType getType() { return type; }
    public int getPrice() { return price; }
    public String getAddress() { return address; }
    public boolean isShared() { return isShared; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
