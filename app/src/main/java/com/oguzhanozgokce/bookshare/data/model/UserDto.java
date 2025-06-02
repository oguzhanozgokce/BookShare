package com.oguzhanozgokce.bookshare.data.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class UserDto {
    public String userId;
    public String name;
    public String email;
    public List<String> myListingsIds;
    public List<String> savedListingsIds;
    public List<PurchaseRecordDto> purchaseHistory; // DTO listesi
    public List<BorrowRecordDto> borrowHistory;   // DTO listesi
    public Map<String, String> fcmTokens;

    public UserDto() {
        myListingsIds = new ArrayList<>();
        savedListingsIds = new ArrayList<>();
        purchaseHistory = new ArrayList<>();
        borrowHistory = new ArrayList<>();
        fcmTokens = new HashMap<>();
    }

}