package com.oguzhanozgokce.bookshare.domain;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.util.List;


public interface BookRepository {
    Result<List<Listing>, FirebaseError> getSaleListings();
    Result<List<Listing>, FirebaseError> getRentListings();
    Result<List<Listing>, FirebaseError> getAllListings();
}
