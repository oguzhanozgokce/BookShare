package com.oguzhanozgokce.bookshare.domain.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class User {
    private String userId;
    private String name;
    private String email;
    private List<String> myListingsIds; // Sadece ID'leri tutmak daha basit olabilir
    private List<String> savedListingsIds;
    private List<PurchaseRecord> purchaseHistory; // Domain model listesi
    private List<BorrowRecord> borrowHistory;     // Domain model listesi
    private Map<String, String> fcmTokens;


    public User() {
        this.myListingsIds = new ArrayList<>();
        this.savedListingsIds = new ArrayList<>();
        this.purchaseHistory = new ArrayList<>();
        this.borrowHistory = new ArrayList<>();
        this.fcmTokens = new HashMap<>();
    }

    public User(String name, String email, Map<String, Listing> listings) {
        this.name = name;
        this.email = email;
        this.myListingsIds = new ArrayList<>();
        this.savedListingsIds = new ArrayList<>();
        this.purchaseHistory = new ArrayList<>();
        this.borrowHistory = new ArrayList<>();
        this.fcmTokens = new HashMap<>();
    }

    public User(String name, String email, Map<String, Listing> listings, Map<String, Boolean> savedListings) {
        this.name = name;
        this.email = email;
        this.myListingsIds = new ArrayList<>();
        this.savedListingsIds = new ArrayList<>();
        this.purchaseHistory = new ArrayList<>();
        this.borrowHistory = new ArrayList<>();
        this.fcmTokens = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getMyListingsIds() {
        return myListingsIds;
    }

    public List<String> getSavedListingsIds() {
        return savedListingsIds;
    }

    public List<PurchaseRecord> getPurchaseHistory() {
        return purchaseHistory;
    }

    public List<BorrowRecord> getBorrowHistory() {
        return borrowHistory;
    }

    public Map<String, String> getFcmTokens() {
        return fcmTokens;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMyListingsIds(List<String> myListingsIds) {
        this.myListingsIds = myListingsIds;
    }

    public void setSavedListingsIds(List<String> savedListingsIds) {
        this.savedListingsIds = savedListingsIds;
    }

    public void setPurchaseHistory(List<PurchaseRecord> purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public void setBorrowHistory(List<BorrowRecord> borrowHistory) {
        this.borrowHistory = borrowHistory;
    }

    public void setFcmTokens(Map<String, String> fcmTokens) {
        this.fcmTokens = fcmTokens;
    }
}
