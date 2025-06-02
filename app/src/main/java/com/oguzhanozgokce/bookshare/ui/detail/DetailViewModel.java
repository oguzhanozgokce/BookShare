package com.oguzhanozgokce.bookshare.ui.detail;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;
import com.oguzhanozgokce.bookshare.domain.model.Listing;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import android.os.Handler;
import android.os.Looper;

@HiltViewModel
public class DetailViewModel extends ViewModel {
    private static final String TAG = "DetailViewModel";
    private final BookRepository bookRepository;
    private final AuthRepository authRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<DetailState> _uiState = new MutableLiveData<>();
    public LiveData<DetailState> getUiState() {
        return _uiState;
    }

    private Listing currentListing;

    @Inject
    public DetailViewModel(BookRepository bookRepository, AuthRepository authRepository) {
        this.bookRepository = bookRepository;
        this.authRepository = authRepository;
    }

    public void setListing(Listing listing) {
        this.currentListing = listing;
        if (listing != null) {
            checkIfSaved();
        }
    }

    public void checkIfSaved() {
        if (currentListing == null) return;
        String currentUserId = authRepository.getCurrentUserId();
        if (currentUserId == null) {
            _uiState.setValue(DetailState.success(currentListing, false)); // Kayıtlı değil olarak göster
            return;
        }

        executorService.execute(() -> {
            Result<Boolean, FirebaseError> result = bookRepository.isListingSaved(currentListing.getId(), currentUserId);
            mainHandler.post(() -> {
                if (result.isSuccess()) {
                    _uiState.setValue(DetailState.success(currentListing, result.getData()));
                } else {
                    Log.e(TAG, "Error checking saved state: " + result.getError());
                    _uiState.setValue(DetailState.success(currentListing, false)); // Hata durumunda kayıtlı değil
                }
            });
        });
    }

    public void toggleSaveListing() {
        if (currentListing == null) {
            Log.e(TAG, "currentListing is null, cannot toggle save.");
            return;
        }
        String currentUserId = authRepository.getCurrentUserId();
        if (currentUserId == null) {
            Log.e(TAG, "User not authenticated, cannot save listing");
            // Kullanıcıya hata mesajı gösterilebilir veya login'e yönlendirilebilir.
            // Şimdilik sadece state'i güncellemeyelim.
            return;
        }

        Log.d(TAG, "Toggling save for listing: " + currentListing.getId() + " with user: " + currentUserId);

        DetailState currentState = _uiState.getValue();
        boolean optimisticIsSaved = currentState != null && currentState.isSaved();

        _uiState.setValue(DetailState.success(currentListing, !optimisticIsSaved));


        executorService.execute(() -> {
            Result<Boolean, FirebaseError> result = bookRepository.toggleSaveListing(currentListing.getId(), currentUserId);
            mainHandler.post(() -> {
                if (result.isSuccess()) {
                    boolean newState = result.getData();
                    Log.d(TAG, "Toggle successful, new state: " + newState);
                    if (_uiState.getValue() != null) {
                        _uiState.setValue(_uiState.getValue().withSavedStatus(newState));
                    }
                } else {
                    Log.e(TAG, "Toggle failed: " + result.getError());
                    if (_uiState.getValue() != null) {
                        _uiState.setValue(_uiState.getValue().withSavedStatus(optimisticIsSaved));
                    }
                }
            });
        });
    }

    public Listing getCurrentListing() {
        return currentListing;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
