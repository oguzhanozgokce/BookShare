package com.oguzhanozgokce.bookshare.ui.detail;

import androidx.annotation.Nullable;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

public class DetailState {
    private final boolean isLoading;
    @Nullable
    private final Listing listing;
    @Nullable
    private final String error;
    private final boolean isSaved;

    public DetailState(boolean isLoading, @Nullable Listing listing, @Nullable String error, boolean isSaved) {
        this.isLoading = isLoading;
        this.listing = listing;
        this.error = error;
        this.isSaved = isSaved;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Nullable
    public Listing getListing() {
        return listing;
    }

    @Nullable
    public String getError() {
        return error;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public static DetailState loading() {
        return new DetailState(true, null, null, false);
    }

    public static DetailState success(Listing listing, boolean isSaved) {
        return new DetailState(false, listing, null, isSaved);
    }

    public static DetailState error(String error) {
        return new DetailState(false, null, error, false);
    }

    public DetailState withSavedStatus(boolean newSavedStatus) {
        return new DetailState(isLoading, listing, error, newSavedStatus);
    }
}
