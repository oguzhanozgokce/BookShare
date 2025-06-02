package com.oguzhanozgokce.bookshare.ui.home;

import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeUiState {
    private final boolean isLoading;
    private final List<Listing> listings;
    private final FirebaseError error;
    private final Set<String> savedListingIds;

    public HomeUiState(boolean isLoading, List<Listing> listings, FirebaseError error, Set<String> savedListingIds) {
        this.isLoading = isLoading;
        this.listings = listings;
        this.error = error;
        this.savedListingIds = savedListingIds != null ? savedListingIds : new HashSet<>();
    }

    public static HomeUiState initial() {
        return new HomeUiState(false, null, null, new HashSet<>());
    }

    public HomeUiState withLoading() {
        return new HomeUiState(true, null, null, savedListingIds);
    }

    public HomeUiState withData(List<Listing> listings) {
        return new HomeUiState(false, listings, null, savedListingIds);
    }

    public HomeUiState withError(FirebaseError error) {
        return new HomeUiState(false, null, error, savedListingIds);
    }

    public HomeUiState withSavedListing(String listingId) {
        Set<String> newSavedIds = new HashSet<>(savedListingIds);
        newSavedIds.add(listingId);
        return new HomeUiState(isLoading, listings, error, newSavedIds);
    }

    public HomeUiState withUnsavedListing(String listingId) {
        Set<String> newSavedIds = new HashSet<>(savedListingIds);
        newSavedIds.remove(listingId);
        return new HomeUiState(isLoading, listings, error, newSavedIds);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public List<Listing> getListings() {
        return listings;
    }

    public FirebaseError getError() {
        return error;
    }

    public Set<String> getSavedListingIds() {
        return savedListingIds;
    }

    public boolean isListingSaved(String listingId) {
        return savedListingIds.contains(listingId);
    }
}
