package com.oguzhanozgokce.bookshare.ui.home;

import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.util.List;

public class HomeUiState {
    private final boolean isLoading;
    private final List<Listing> listings;
    private final String error;

    public HomeUiState(boolean isLoading, List<Listing> listings, String error) {
        this.isLoading = isLoading;
        this.listings = listings;
        this.error = error;
    }

    public boolean isLoading() { return isLoading; }
    public List<Listing> getListings() { return listings; }
    public String getError() { return error; }
}
