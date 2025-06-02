package com.oguzhanozgokce.bookshare.domain.model;

import com.oguzhanozgokce.bookshare.data.model.ListingType;

import java.io.Serializable;
import java.util.List;

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
    private final Genre genre;
    private final String startDate;
    private final String endDate;
    private final String createdAt;
    private final String updatedAt;
    private final String userId;
    private final String author;
    private final String isbn;
    private final List<String> imageUrls;
    private final String publisher;
    private final int publishYear;
    private final int pageCount;
    private final String language;

    public Listing(String id, String title, String description, String condition, String location,
                   ListingType type, int price, String address, boolean isShared, Genre genre,
                   String startDate, String endDate, String createdAt, String updatedAt,
                   String userId, String author, String isbn, List<String> imageUrls,
                   String publisher, int publishYear, int pageCount, String language) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.location = location;
        this.type = type;
        this.price = price;
        this.address = address;
        this.isShared = isShared;
        this.genre = genre;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.author = author;
        this.isbn = isbn;
        this.imageUrls = imageUrls;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.pageCount = pageCount;
        this.language = language;
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
    public Genre getGenre() { return genre; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public String getUserId() {
        return userId;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getLanguage() {
        return language;
    }
}
