package com.oguzhanozgokce.bookshare.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.data.SafeCall;
import com.oguzhanozgokce.bookshare.data.mapper.ListingMapper;
import com.oguzhanozgokce.bookshare.data.mapper.TransactionMapper;
import com.oguzhanozgokce.bookshare.data.model.BorrowRecordDto;
import com.oguzhanozgokce.bookshare.data.model.ListingDto;
import com.oguzhanozgokce.bookshare.data.model.PurchaseRecordDto;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.BorrowRecord;
import com.oguzhanozgokce.bookshare.domain.model.Listing;
import com.oguzhanozgokce.bookshare.domain.model.PurchaseRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BookRepositoryImpl implements BookRepository {
    private final FirebaseFirestore db;

    @Inject
    public BookRepositoryImpl(FirebaseFirestore db) {
        this.db = db;
    }

    private ListingDto convertToDto(Object value) {
        return new Gson().fromJson(new Gson().toJson(value), ListingDto.class);
    }

    @Override
    public Result<List<Listing>, FirebaseError> getFilteredListings(String userId, boolean forSale) {
        return SafeCall.safeCall(() -> {
            QuerySnapshot snapshots = Tasks.await(db.collection("users").get());
            List<Listing> filteredListings = new ArrayList<>();

            for (QueryDocumentSnapshot doc : snapshots) {
                Map<String, Object> listingsMap = (Map<String, Object>) doc.get("listings");
                if (listingsMap != null) {
                    for (Object value : listingsMap.values()) {
                        ListingDto dto = convertToDto(value);
                        if (dto != null && dto.type != null) {
                            boolean isSaleType = dto.type.equals("FOR_SALE");
                            if (forSale == isSaleType) {
                                filteredListings.add(ListingMapper.toDomain(dto));
                            }
                        }
                    }
                }
            }
            return filteredListings;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Boolean, FirebaseError> toggleSaveListing(String listingId, String userId) {
        return SafeCall.safeCall(() -> {
            Log.d("BookRepository", "Toggling save for listing " + listingId + " and user " + userId);

            // User document'ını al
            DocumentSnapshot userDoc = Tasks.await(db.collection("users").document(userId).get());

            if (!userDoc.exists()) {
                Log.e("BookRepository", "User document not found: " + userId + ". User should be registered first.");
                throw new Exception("User document not found: " + userId);
            }

            // savedListings alanının var olduğundan emin ol
            @SuppressWarnings("unchecked")
            Map<String, Object> userData = userDoc.getData();
            if (userData != null && !userData.containsKey("savedListings")) {
                // savedListings alanını oluştur
                Map<String, Object> initialUpdate = new HashMap<>();
                initialUpdate.put("savedListings", new HashMap<String, Boolean>());
                Tasks.await(db.collection("users").document(userId).update(initialUpdate));
                Log.d("BookRepository", "Created savedListings field for user: " + userId);

                // Document'ı tekrar al
                userDoc = Tasks.await(db.collection("users").document(userId).get());
            }

            // Mevcut durumu kontrol et
            @SuppressWarnings("unchecked")
            Map<String, Boolean> savedListings = (Map<String, Boolean>) userDoc.get("savedListings");
            boolean isCurrentlySaved = savedListings != null &&
                    savedListings.containsKey(listingId) &&
                    Boolean.TRUE.equals(savedListings.get(listingId));

            Log.d("BookRepository", "Current save state: " + isCurrentlySaved);

            // Toggle işlemini yap
            Map<String, Object> updates = new HashMap<>();
            if (isCurrentlySaved) {
                // Unsave
                updates.put("savedListings." + listingId, FieldValue.delete());
                Log.d("BookRepository", "Unsaving listing");
            } else {
                // Save
                updates.put("savedListings." + listingId, true);
                Log.d("BookRepository", "Saving listing");
            }

            Tasks.await(db.collection("users").document(userId).update(updates));

            boolean newState = !isCurrentlySaved;
            Log.d("BookRepository", "Toggle successful, new state: " + newState);
            return newState;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<List<Listing>, FirebaseError> getSaleListings() {
        return SafeCall.safeCall(() -> {
            QuerySnapshot snapshots = Tasks.await(db.collection("users").get());
            List<Listing> saleListings = new ArrayList<>();

            for (QueryDocumentSnapshot doc : snapshots) {
                Map<String, Object> listingsMap = (Map<String, Object>) doc.get("listings");
                if (listingsMap != null) {
                    for (Object value : listingsMap.values()) {
                        ListingDto dto = convertToDto(value);
                        if (dto != null && dto.type != null && dto.type.equals("FOR_SALE")) {
                            saleListings.add(ListingMapper.toDomain(dto));
                        }
                    }
                }
            }
            return saleListings;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<List<Listing>, FirebaseError> getRentListings() {
        return SafeCall.safeCall(() -> {
            QuerySnapshot snapshots = Tasks.await(db.collection("users").get());
            List<Listing> rentListings = new ArrayList<>();

            for (QueryDocumentSnapshot doc : snapshots) {
                Map<String, Object> listingsMap = (Map<String, Object>) doc.get("listings");
                if (listingsMap != null) {
                    for (Object value : listingsMap.values()) {
                        ListingDto dto = convertToDto(value);
                        if (dto != null && dto.type != null && dto.type.equals("FOR_RENT")) {
                            rentListings.add(ListingMapper.toDomain(dto));
                        }
                    }
                }
            }
            return rentListings;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<List<Listing>, FirebaseError> getAllListings() {
        return SafeCall.safeCall(() -> {
            QuerySnapshot snapshots = Tasks.await(db.collection("users").get());
            List<Listing> allListings = new ArrayList<>();

            for (QueryDocumentSnapshot doc : snapshots) {
                Map<String, Object> listingsMap = (Map<String, Object>) doc.get("listings");
                if (listingsMap != null) {
                    for (Object value : listingsMap.values()) {
                        ListingDto dto = convertToDto(value);
                        if (dto != null) {
                            allListings.add(ListingMapper.toDomain(dto));
                        }
                    }
                }
            }
            return allListings;
        });
    }

    @Override
    public Result<List<Listing>, FirebaseError> getSavedListings(String userId) {
        return SafeCall.safeCall(() -> {
            DocumentSnapshot userDoc = Tasks.await(db.collection("users").document(userId).get());

            @SuppressWarnings("unchecked")
            Map<String, Boolean> savedListings = (Map<String, Boolean>) userDoc.get("savedListings");

            if (savedListings == null || savedListings.isEmpty()) {
                return new ArrayList<>();
            }

            // Tüm ilanları getir ve kaydedilenleri filtrele
            Result<List<Listing>, FirebaseError> allListingsResult = getAllListings();
            if (allListingsResult.isSuccess()) {
                List<Listing> allListings = allListingsResult.getData();
                List<Listing> saved = new ArrayList<>();

                for (Listing listing : allListings) {
                    if (savedListings.containsKey(listing.getId()) &&
                            Boolean.TRUE.equals(savedListings.get(listing.getId()))) {
                        saved.add(listing);
                    }
                }
                return saved;
            }
            return new ArrayList<>();
        });
    }

    @Override
    public Result<Boolean, FirebaseError> isListingSaved(String listingId, String userId) {
        return SafeCall.safeCall(() -> {
            Log.d("BookRepository", "Checking if listing " + listingId + " is saved for user " + userId);

            DocumentSnapshot doc = Tasks.await(db.collection("users").document(userId).get());

            @SuppressWarnings("unchecked")
            Map<String, Boolean> savedListings = (Map<String, Boolean>) doc.get("savedListings");

            boolean isSaved = savedListings != null && savedListings.containsKey(listingId) && savedListings.get(listingId);
            Log.d("BookRepository", "Listing saved status: " + isSaved);
            return isSaved;
        });
    }

    @Override
    public Task<Void> addPurchaseRecordToUser(String userId, PurchaseRecord purchaseRecord) {
        PurchaseRecordDto dto = TransactionMapper.toPurchaseRecordDto(purchaseRecord);
        if (dto == null || dto.transactionId == null || dto.transactionId.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("PurchaseRecordDto or its transactionId cannot be null or empty"));
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("purchaseHistory." + dto.transactionId, dto);

        return db.collection("users").document(userId).update(updates);
    }

    @Override
    public Task<Void> addBorrowRecordToUser(String userId, BorrowRecord borrowRecord) {
        BorrowRecordDto dto = TransactionMapper.toBorrowRecordDto(borrowRecord);
        if (dto == null || dto.transactionId == null || dto.transactionId.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("BorrowRecordDto or its transactionId cannot be null or empty"));
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("borrowHistory." + dto.transactionId, dto);

        return db.collection("users").document(userId).update(updates);
    }

    @Override
    public Task<Void> removeListing(String userId, String listingId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("listings." + listingId, FieldValue.delete());

        return db.collection("users").document(userId).update(updates);
    }

    @Override
    public Result<String, FirebaseError> getListingOwnerId(String listingId) {
        return SafeCall.safeCall(() -> {
            QuerySnapshot snapshots = Tasks.await(db.collection("users").get());

            for (QueryDocumentSnapshot doc : snapshots) {
                Map<String, Object> listingsMap = (Map<String, Object>) doc.get("listings");
                if (listingsMap != null && listingsMap.containsKey(listingId)) {
                    return doc.getId();
                }
            }
            throw new Exception("Listing owner not found");
        });
    }
}
