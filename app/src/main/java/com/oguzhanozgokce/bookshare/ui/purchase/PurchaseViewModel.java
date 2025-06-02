package com.oguzhanozgokce.bookshare.ui.purchase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.model.PurchaseRecord;

import java.util.Date;
import java.util.UUID;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class PurchaseViewModel extends ViewModel {

    private final MutableLiveData<PurchaseState> _state = new MutableLiveData<>(new PurchaseState());
    public final LiveData<PurchaseState> state = _state;

    private final SessionManager sessionManager;
    private final BookRepository bookRepository;

    @Inject
    public PurchaseViewModel(SessionManager sessionManager, BookRepository bookRepository) {
        this.sessionManager = sessionManager;
        this.bookRepository = bookRepository;
    }

    // Update form fields
    public void updateFullName(String fullName) {
        PurchaseState currentState = getCurrentState();
        currentState.setFullName(fullName);
        validateForm();
        _state.setValue(currentState);
    }

    public void updatePhone(String phone) {
        PurchaseState currentState = getCurrentState();
        currentState.setPhone(phone);
        validateForm();
        _state.setValue(currentState);
    }

    public void updateAddress(String address) {
        PurchaseState currentState = getCurrentState();
        currentState.setAddress(address);
        validateForm();
        _state.setValue(currentState);
    }

    public void updateCity(String city) {
        PurchaseState currentState = getCurrentState();
        currentState.setCity(city);
        validateForm();
        _state.setValue(currentState);
    }

    public void updatePostalCode(String postalCode) {
        PurchaseState currentState = getCurrentState();
        currentState.setPostalCode(postalCode);
        validateForm();
        _state.setValue(currentState);
    }

    public void updatePaymentMethod(PurchaseState.PaymentMethod paymentMethod) {
        PurchaseState currentState = getCurrentState();
        currentState.setPaymentMethod(paymentMethod);
        validatePayment();
        _state.setValue(currentState);
    }

    public void updateCardNumber(String cardNumber) {
        PurchaseState currentState = getCurrentState();
        currentState.setCardNumber(cardNumber);
        validatePayment();
        _state.setValue(currentState);
    }

    public void updateExpiry(String expiry) {
        PurchaseState currentState = getCurrentState();
        currentState.setExpiry(expiry);
        validatePayment();
        _state.setValue(currentState);
    }

    public void updateCvv(String cvv) {
        PurchaseState currentState = getCurrentState();
        currentState.setCvv(cvv);
        validatePayment();
        _state.setValue(currentState);
    }

    public void updateCardHolderName(String cardHolderName) {
        PurchaseState currentState = getCurrentState();
        currentState.setCardHolderName(cardHolderName);
        validatePayment();
        _state.setValue(currentState);
    }

    public void setBookInfo(String bookTitle, String bookId, double price) {
        PurchaseState currentState = getCurrentState();
        currentState.setBookTitle(bookTitle);
        currentState.setBookId(bookId);
        currentState.setPrice(price);
        _state.setValue(currentState);
    }

    // Validation methods
    private void validateForm() {
        PurchaseState currentState = getCurrentState();
        boolean isValid = currentState.isDeliveryInfoComplete() && isValidPhone(currentState.getPhone());
        currentState.setFormValid(isValid);
        // Don't set error message here - only set it when button is clicked
    }

    private void validatePayment() {
        PurchaseState currentState = getCurrentState();

        if (currentState.getPaymentMethod() == PurchaseState.PaymentMethod.CASH_ON_DELIVERY) {
            currentState.setPaymentValid(true);
            return;
        }

        boolean isValid = currentState.isCardInfoComplete() &&
                isValidCardNumber(currentState.getCardNumber()) &&
                isValidCvv(currentState.getCvv());

        currentState.setPaymentValid(isValid);
        // Don't set error message here - only set it when button is clicked
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.trim().length() >= 10;
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) return false;
        String cleanNumber = cardNumber.replaceAll("\\s", "");
        return cleanNumber.length() == 16 && cleanNumber.matches("\\d+");
    }

    private boolean isValidCvv(String cvv) {
        return cvv != null && cvv.trim().length() == 3 && cvv.matches("\\d+");
    }

    // Business logic
    public void completePurchase() {
        PurchaseState currentState = getCurrentState();

        if (!currentState.canCompletePurchase()) {
            if (!currentState.isDeliveryInfoComplete()) {
                currentState.setErrorMessage("Lütfen tüm teslimat bilgilerini doldurun");
            } else if (!isValidPhone(currentState.getPhone())) {
                currentState.setErrorMessage("Geçerli bir telefon numarası girin");
            } else if (!currentState.isPaymentValid()) {
                if (!currentState.isCardInfoComplete()) {
                    currentState.setErrorMessage("Lütfen tüm kart bilgilerini doldurun");
                } else if (!isValidCardNumber(currentState.getCardNumber())) {
                    currentState.setErrorMessage("Kart numarası 16 haneli olmalıdır");
                } else if (!isValidCvv(currentState.getCvv())) {
                    currentState.setErrorMessage("CVV 3 haneli olmalıdır");
                }
            } else {
                currentState.setErrorMessage("Lütfen tüm gerekli alanları doldurun");
            }

            _state.setValue(currentState);
            return;
        }

        currentState.setLoading(true);
        currentState.setErrorMessage("");
        _state.setValue(currentState);

        addPurchaseRecordToUser();
    }

    public void addPurchaseRecordToUser() {
        PurchaseState currentState = getCurrentState();
        String userId = sessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            currentState.setLoading(false);
            currentState.setErrorMessage("Kullanıcı oturumu bulunamadı");
            _state.setValue(currentState);
            return;
        }

        PurchaseRecord purchaseRecord = new PurchaseRecord(
                UUID.randomUUID().toString(),
                currentState.getBookId(),
                currentState.getBookTitle(),
                currentState.getPrice(),
                new Date(),
                currentState.getAddress() + ", " + currentState.getCity() + " " + currentState.getPostalCode(),
                currentState.getPaymentMethod().toString()
        );

        bookRepository.addPurchaseRecordToUser(userId, purchaseRecord)
                .addOnSuccessListener(aVoid -> {
                    // Remove listing after successful purchase record
                    removeListing();
                })
                .addOnFailureListener(e -> {
                    PurchaseState errorState = getCurrentState();
                    errorState.setLoading(false);
                    errorState.setErrorMessage("Satın alma kaydı eklenirken hata oluştu");
                    _state.postValue(errorState);
                });
    }

    private void removeListing() {
        PurchaseState currentState = getCurrentState();
        String listingId = currentState.getBookId();

        // Get listing owner ID first
        new Thread(() -> {
            try {
                var result = bookRepository.getListingOwnerId(listingId);
                if (result.isSuccess()) {
                    String ownerId = result.getData();

                    // Remove listing from owner's listings
                    bookRepository.removeListing(ownerId, listingId)
                            .addOnSuccessListener(aVoid -> {
                                PurchaseState newState = getCurrentState();
                                newState.setLoading(false);
                                newState.setCompleted(true);
                                newState.setErrorMessage("");
                                _state.postValue(newState);
                            })
                            .addOnFailureListener(e -> {
                                PurchaseState errorState = getCurrentState();
                                errorState.setLoading(false);
                                errorState.setErrorMessage("İlan kaldırılırken hata oluştu");
                                _state.postValue(errorState);
                            });
                } else {
                    PurchaseState errorState = getCurrentState();
                    errorState.setLoading(false);
                    errorState.setErrorMessage("İlan sahibi bulunamadı");
                    _state.postValue(errorState);
                }
            } catch (Exception e) {
                PurchaseState errorState = getCurrentState();
                errorState.setLoading(false);
                errorState.setErrorMessage("Bir hata oluştu");
                _state.postValue(errorState);
            }
        }).start();
    }

    public String getFormattedPrice() {
        PurchaseState currentState = getCurrentState();
        return String.format("%.0f ₺", currentState.getPrice());
    }

    public void clearState() {
        PurchaseState currentState = getCurrentState();
        currentState.clearForm();
        _state.setValue(currentState);
    }

    public void clearErrorMessage() {
        PurchaseState currentState = getCurrentState();
        currentState.setErrorMessage("");
        _state.setValue(currentState);
    }

    private PurchaseState getCurrentState() {
        PurchaseState currentState = _state.getValue();
        return currentState != null ? currentState : new PurchaseState();
    }
}
