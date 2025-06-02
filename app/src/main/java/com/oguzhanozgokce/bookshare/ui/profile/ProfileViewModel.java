package com.oguzhanozgokce.bookshare.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.model.BorrowRecord;
import com.oguzhanozgokce.bookshare.domain.model.PurchaseRecord;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final BookRepository bookRepository;

    private final MutableLiveData<ProfileState> _state = new MutableLiveData<>(new ProfileState());
    public final LiveData<ProfileState> state = _state;

    @Inject
    public ProfileViewModel(SessionManager sessionManager, BookRepository bookRepository) {
        this.sessionManager = sessionManager;
        this.bookRepository = bookRepository;
        loadUserProfile();
    }

    private void loadUserProfile() {
        ProfileState currentState = getCurrentState();
        currentState.setLoading(true);
        _state.setValue(currentState);

        String userId = sessionManager.getUserId();
        String email = sessionManager.getEmail();

        if (userId == null || email == null) {
            currentState.setLoading(false);
            currentState.setErrorMessage("Kullanıcı bilgileri bulunamadı");
            _state.setValue(currentState);
            return;
        }

        // Set email immediately
        currentState.setUserEmail(email);
        _state.setValue(currentState);

        // Load transaction histories in background
        new Thread(() -> {
            try {
                var borrowResult = bookRepository.getBorrowHistory(userId);
                var purchaseResult = bookRepository.getPurchaseHistory(userId);

                ProfileState newState = getCurrentState();
                newState.setLoading(false);
                newState.setUserEmail(email);

                if (borrowResult.isSuccess()) {
                    newState.setBorrowedBooks(borrowResult.getData());
                } else {
                    newState.setBorrowedBooks(new ArrayList<>()); // Set empty list on error
                }

                if (purchaseResult.isSuccess()) {
                    newState.setPurchasedBooks(purchaseResult.getData());
                } else {
                    newState.setPurchasedBooks(new ArrayList<>()); // Set empty list on error
                }

                newState.setErrorMessage("");
                _state.postValue(newState);

            } catch (Exception e) {
                ProfileState errorState = getCurrentState();
                errorState.setLoading(false);
                errorState.setBorrowedBooks(new ArrayList<>());
                errorState.setPurchasedBooks(new ArrayList<>());
                errorState.setErrorMessage("İşlem geçmişi yüklenirken hata oluştu");
                _state.postValue(errorState);
            }
        }).start();
    }

    public void logout() {
        sessionManager.clearSession();

        ProfileState currentState = getCurrentState();
        currentState.setLoggedOut(true);
        _state.setValue(currentState);
    }

    public void refreshProfile() {
        loadUserProfile();
    }

    public void clearErrorMessage() {
        ProfileState currentState = getCurrentState();
        currentState.setErrorMessage("");
        _state.setValue(currentState);
    }

    private ProfileState getCurrentState() {
        ProfileState currentState = _state.getValue();
        return currentState != null ? currentState : new ProfileState();
    }

    // ProfileState class
    public static class ProfileState {
        private boolean isLoading = false;
        private boolean isLoggedOut = false;
        private String errorMessage = "";
        private String userEmail = "";
        private List<BorrowRecord> borrowedBooks;
        private List<PurchaseRecord> purchasedBooks;

        // Getters and setters
        public boolean isLoading() {
            return isLoading;
        }

        public void setLoading(boolean loading) {
            isLoading = loading;
        }

        public boolean isLoggedOut() {
            return isLoggedOut;
        }

        public void setLoggedOut(boolean loggedOut) {
            isLoggedOut = loggedOut;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public List<BorrowRecord> getBorrowedBooks() {
            return borrowedBooks;
        }

        public void setBorrowedBooks(List<BorrowRecord> borrowedBooks) {
            this.borrowedBooks = borrowedBooks;
        }

        public List<PurchaseRecord> getPurchasedBooks() {
            return purchasedBooks;
        }

        public void setPurchasedBooks(List<PurchaseRecord> purchasedBooks) {
            this.purchasedBooks = purchasedBooks;
        }

        public int getBorrowedBooksCount() {
            return borrowedBooks != null ? borrowedBooks.size() : 0;
        }

        public int getPurchasedBooksCount() {
            return purchasedBooks != null ? purchasedBooks.size() : 0;
        }

        public boolean hasBorrowedBooks() {
            return borrowedBooks != null && !borrowedBooks.isEmpty();
        }

        public boolean hasPurchasedBooks() {
            return purchasedBooks != null && !purchasedBooks.isEmpty();
        }
    }
}
