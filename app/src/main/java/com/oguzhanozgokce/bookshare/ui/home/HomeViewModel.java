package com.oguzhanozgokce.bookshare.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.common.ResultUtils;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.util.List;
import java.util.function.Consumer;

import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final BookRepository bookRepository;

    private final MutableLiveData<HomeUiState> uiState = new MutableLiveData<>();

    public LiveData<HomeUiState> getUiState() {
        return uiState;
    }

    @Inject
    public HomeViewModel(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void loadAllListings() {
        uiState.setValue(new HomeUiState(true, null, null));
        Result<List<Listing>, FirebaseError> result = bookRepository.getAllListings();
        result.fold(
                listings -> uiState.setValue(new HomeUiState(false, listings, null)),
                error -> uiState.setValue(new HomeUiState(false, null, error))
        );
    }

    public void loadSaleListings() {
        Result<List<Listing>, FirebaseError> result = bookRepository.getSaleListings();
        ResultUtils.fold(
                result,
                successListings -> {
                    for (Listing listing : successListings) {
                        System.out.println("FOR SALE -> " + listing.getTitle());
                    }
                },
                errorMessage -> {
                    System.err.println("Error loading sale listings: " + errorMessage);
                }
        );
    }

    public void loadRentListings() {
        Result<List<Listing>, FirebaseError> result = bookRepository.getSaleListings();

        Consumer<List<Listing>> onSuccess = listings -> {
            for (Listing listing : listings) {
                System.out.println("FOR SALE -> " + listing.getTitle());
            }
        };

        Consumer<String> onError = errorMessage -> {
            System.err.println("Error: " + errorMessage);
        };

        result.fold(onSuccess, onError);
    }
}

