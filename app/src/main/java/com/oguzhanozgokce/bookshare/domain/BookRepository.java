package com.oguzhanozgokce.bookshare.domain;

import com.google.android.gms.tasks.Task;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.BorrowRecord;
import com.oguzhanozgokce.bookshare.domain.model.Listing;
import com.oguzhanozgokce.bookshare.domain.model.PurchaseRecord;

import java.util.List;


public interface BookRepository {
    Result<List<Listing>, FirebaseError> getSaleListings();
    Result<List<Listing>, FirebaseError> getRentListings();
    Result<List<Listing>, FirebaseError> getAllListings();

    Result<List<Listing>, FirebaseError> getFilteredListings(String userId, boolean forSale);

    Result<Boolean, FirebaseError> toggleSaveListing(String listingId, String userId);
    Result<List<Listing>, FirebaseError> getSavedListings(String userId);
    Result<Boolean, FirebaseError> isListingSaved(String listingId, String userId);
    Task<Void> addPurchaseRecordToUser(String userId, PurchaseRecord purchaseRecord);
    Task<Void> addBorrowRecordToUser(String userId, BorrowRecord borrowRecord);

    Task<Void> removeListing(String userId, String listingId);

    Result<String, FirebaseError> getListingOwnerId(String listingId);

    Result<List<PurchaseRecord>, FirebaseError> getPurchaseHistory(String userId);

    Result<List<BorrowRecord>, FirebaseError> getBorrowHistory(String userId);
}
