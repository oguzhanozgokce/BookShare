package com.oguzhanozgokce.bookshare.ui.home;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.common.ResultUtils;
import com.oguzhanozgokce.bookshare.data.model.ListingType;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";
    private final BookRepository bookRepository;
    private final AuthRepository authRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<HomeUiState> uiState = new MutableLiveData<>(HomeUiState.initial());

    public LiveData<HomeUiState> getUiState() {
        return uiState;
    }

    @Inject
    public HomeViewModel(BookRepository bookRepository, AuthRepository authRepository) {
        this.bookRepository = bookRepository;
        this.authRepository = authRepository;
    }

    public void loadAllListings() {
        updateState(HomeUiState::withLoading);

        executorService.execute(() -> {
            Result<List<Listing>, FirebaseError> result = bookRepository.getAllListings();

            mainHandler.post(() -> {
                if (result.isSuccess()) {
                    updateState(state -> state.withData(result.getData()));
                } else {
                    updateState(state -> state.withError(result.getError()));
                }
            });
        });
    }

    public void loadListingsByType(ListingType type) {
        String currentUserId = authRepository.getCurrentUserId();
        if (currentUserId == null) {
            Log.e(TAG, "User not authenticated");
            updateState(state -> state.withError(null));
            return;
        }

        updateState(HomeUiState::withLoading);

        executorService.execute(() -> {
            boolean forSale = (type == ListingType.FOR_SALE);
            Result<List<Listing>, FirebaseError> result = bookRepository.getFilteredListings(currentUserId, forSale);

            mainHandler.post(() -> {
                if (result.isSuccess()) {
                    updateState(state -> state.withData(result.getData()));
                } else {
                    updateState(state -> state.withError(result.getError()));
                }
            });
        });
    }

    public void toggleSaveListing(String listingId) {
        String currentUserId = authRepository.getCurrentUserId();
        if (currentUserId == null) {
            Log.e(TAG, "User not authenticated, cannot save listing");
            return;
        }

        Log.d(TAG, "Toggling save for listing: " + listingId + " with user: " + currentUserId);

        // UI'da anında güncelleme yap
        HomeUiState currentState = uiState.getValue();
        if (currentState != null) {
            boolean isCurrentlySaved = currentState.isListingSaved(listingId);

            Log.d(TAG, "Current save state: " + isCurrentlySaved);

            if (isCurrentlySaved) {
                updateState(state -> state.withUnsavedListing(listingId));
            } else {
                updateState(state -> state.withSavedListing(listingId));
            }
        }

        // Background'da Firebase toggle işlemini yap
        executorService.execute(() -> {
            Result<Boolean, FirebaseError> result = bookRepository.toggleSaveListing(listingId, currentUserId);

            if (result.isSuccess()) {
                boolean newState = result.getData();
                Log.d(TAG, "Toggle successful, new state: " + newState);

                // UI state'i Firebase sonucuyla senkronize et
                mainHandler.post(() -> {
                    if (newState) {
                        updateState(state -> state.withSavedListing(listingId));
                    } else {
                        updateState(state -> state.withUnsavedListing(listingId));
                    }
                });
            } else {
                Log.e(TAG, "Toggle failed: " + result.getError());
                // Hata durumunda UI'ı geri al
                mainHandler.post(() -> {
                    HomeUiState currentState2 = uiState.getValue();
                    if (currentState2 != null) {
                        boolean currentSaved = currentState2.isListingSaved(listingId);
                        if (currentSaved) {
                            updateState(state -> state.withUnsavedListing(listingId));
                        } else {
                            updateState(state -> state.withSavedListing(listingId));
                        }
                    }
                });
            }
        });
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
        Result<List<Listing>, FirebaseError> result = bookRepository.getRentListings();

        Consumer<List<Listing>> onSuccess = listings -> {
            for (Listing listing : listings) {
                System.out.println("FOR RENT -> " + listing.getTitle());
            }
        };

        Consumer<String> onError = errorMessage -> {
            System.err.println("Error: " + errorMessage);
        };

        result.fold(onSuccess, onError);
    }

    private void updateState(Function<HomeUiState, HomeUiState> updater) {
        HomeUiState current = uiState.getValue();
        if (current == null) {
            current = HomeUiState.initial();
        }
        uiState.setValue(updater.apply(current));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
