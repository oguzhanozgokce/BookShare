package com.oguzhanozgokce.bookshare.ui.home;

import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.util.List;

public class HomeUiState {
    private final boolean isLoading;
    private final List<Listing> listings;
    private final FirebaseError error;

    public HomeUiState(boolean isLoading, List<Listing> listings, FirebaseError error) {
        this.isLoading = isLoading;
        this.listings = listings;
        this.error = error;
    }

    public static HomeUiState initial() {
        return new HomeUiState(false, null, null);
    }

    public HomeUiState withLoading() {
        return new HomeUiState(true, null, null);
    }

    public HomeUiState withData(List<Listing> listings) {
        return new HomeUiState(false, listings, null);
    }

    public HomeUiState withError(FirebaseError error) {
        return new HomeUiState(false, null, error);
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
}
