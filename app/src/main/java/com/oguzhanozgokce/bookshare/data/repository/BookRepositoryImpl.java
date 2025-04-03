package com.oguzhanozgokce.bookshare.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.data.SafeCall;
import com.oguzhanozgokce.bookshare.data.mapper.ListingMapper;
import com.oguzhanozgokce.bookshare.data.model.ListingDto;
import com.oguzhanozgokce.bookshare.data.model.ListingType;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.Genre;
import com.oguzhanozgokce.bookshare.domain.model.Listing;
import com.oguzhanozgokce.bookshare.domain.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

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
    public Result<List<Listing>, FirebaseError> getSaleListings() {
        return SafeCall.safeCall(() -> {
            Task<QuerySnapshot> task = db.collection("users").get();
            QuerySnapshot snapshots = Tasks.await(task);
            List<Listing> saleListings = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                User user = doc.toObject(User.class);
                Map<String, Listing> listingsMap = user.getListings();
                if (listingsMap != null) {
                    for (Listing listing : listingsMap.values()) {
                        if (listing.getType() == ListingType.FOR_SALE) {
                            saleListings.add(listing);
                        }
                    }
                }
            }
            return saleListings;
        });
    }

    @Override
    public Result<List<Listing>, FirebaseError> getRentListings() {
        return SafeCall.safeCall(() -> {
            Task<QuerySnapshot> task = db.collection("users").get();
            QuerySnapshot snapshots = Tasks.await(task);
            List<Listing> rentListings = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                User user = doc.toObject(User.class);
                Map<String, Listing> listingsMap = user.getListings();
                if (listingsMap != null) {
                    for (Listing listing : listingsMap.values()) {
                        if (listing.getType() == ListingType.FOR_RENT) {
                            rentListings.add(listing);
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
        return SafeCall.safeCall(() ->
                Executors.newSingleThreadExecutor().submit(() -> {
                    Task<QuerySnapshot> task = db.collection("users").get();
                    QuerySnapshot snapshots = Tasks.await(task);
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
                }).get()
        );
    }


    private User createSampleUser1() {
        Listing listing1 = new Listing(
                "",
                "The Pragmatic Programmer",
                "Must-read for every developer.",
                "Like New",
                "Bursa",
                ListingType.FOR_SALE,
                180,
                "Nilüfer, Bursa",
                false,
                Genre.EDUCATION,
                "2025-04-10",
                "2025-04-20",
                "2025-04-05",
                "2025-04-05"
        );


        Listing listing2 = new Listing(
                "",
                "UX Design Handbook",
                "Great book on modern UX/UI principles.",
                "Used",
                "Eskişehir",
                ListingType.FOR_RENT,
                0,
                "Odunpazarı, Eskişehir",
                true,
                Genre.FANTASY,
                "2025-04-08",
                "2025-04-15",
                "2025-04-05",
                "2025-04-05"
        );

        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();

        Map<String, Listing> listings = new HashMap<>();
        listings.put(id1, listing1);
        listings.put(id2, listing2);

        return new User("Charlie", "charlie@example.com", listings);
    }

    private User createSampleUser2() {
        Listing listing1 = new Listing(
                "",
                "Data Structures in Java",
                "Perfect for CS students.",
                "New",
                "Antalya",
                ListingType.FOR_SALE,
                220,
                "Konyaaltı, Antalya",
                false,
                Genre.SCIENCE,
                "2025-04-12",
                "2025-04-22",
                "2025-04-06",
                "2025-04-06"
        );

        Listing listing2 = new Listing(
                "",
                "Machine Learning Crash Course",
                "Compact and beginner-friendly.",
                "Used",
                "Mersin",
                ListingType.FOR_RENT,
                0,
                "Yenişehir, Mersin",
                true,
                Genre.FICTION,
                "2025-04-10",
                "2025-04-17",
                "2025-04-06",
                "2025-04-06"
        );

        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();

        Map<String, Listing> listings = new HashMap<>();
        listings.put(id1, listing1);
        listings.put(id2, listing2);

        return new User("Deniz", "deniz@example.com", listings);
    }

    public void seedInitialData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        User user1 = createSampleUser1();
        User user2 = createSampleUser2();

        db.collection("users").add(user1)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firestore", "User1 created with ID: " + documentReference.getId())
                )
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error adding user1", e)
                );

        db.collection("users").add(user2)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firestore", "User2 created with ID: " + documentReference.getId())
                )
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error adding user2", e)
                );
    }
}
