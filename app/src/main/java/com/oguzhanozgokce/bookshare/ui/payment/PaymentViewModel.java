package com.oguzhanozgokce.bookshare.ui.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.BookRepository;
import com.oguzhanozgokce.bookshare.domain.model.BorrowRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class PaymentViewModel extends ViewModel {

    private final MutableLiveData<PaymentState> _state = new MutableLiveData<>(new PaymentState());
    public final LiveData<PaymentState> state = _state;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final SessionManager sessionManager;
    private final BookRepository bookRepository;

    @Inject
    public PaymentViewModel(SessionManager sessionManager, BookRepository bookRepository) {
        this.sessionManager = sessionManager;
        this.bookRepository = bookRepository;
    }

    // Update form fields
    public void updateFullName(String fullName) {
        PaymentState currentState = getCurrentState();
        currentState.setFullName(fullName);
        validateForm();
        _state.setValue(currentState);
    }

    public void updatePhone(String phone) {
        PaymentState currentState = getCurrentState();
        currentState.setPhone(phone);
        validateForm();
        _state.setValue(currentState);
    }

    public void updateAddress(String address) {
        PaymentState currentState = getCurrentState();
        currentState.setAddress(address);
        validateForm();
        _state.setValue(currentState);
    }

    public void updateCity(String city) {
        PaymentState currentState = getCurrentState();
        currentState.setCity(city);
        validateForm();
        _state.setValue(currentState);
    }

    public void updatePostalCode(String postalCode) {
        PaymentState currentState = getCurrentState();
        currentState.setPostalCode(postalCode);
        validateForm();
        _state.setValue(currentState);
    }

    public void updateStartDate(String startDate) {
        PaymentState currentState = getCurrentState();
        currentState.setStartDate(startDate);
        validateDates();
        _state.setValue(currentState);
    }

    public void updateEndDate(String endDate) {
        PaymentState currentState = getCurrentState();
        currentState.setEndDate(endDate);
        validateDates();
        _state.setValue(currentState);
    }

    public void setBookInfo(String bookTitle, String bookId) {
        PaymentState currentState = getCurrentState();
        currentState.setBookTitle(bookTitle);
        currentState.setBookId(bookId);
        _state.setValue(currentState);
    }

    // Validation methods
    private void validateForm() {
        PaymentState currentState = getCurrentState();
        boolean isValid = currentState.isContactInfoComplete() && isValidPhone(currentState.getPhone());
        currentState.setFormValid(isValid);
        // Don't set error message here - only set it when button is clicked
    }

    public void clearErrorMessage() {
        PaymentState currentState = getCurrentState();
        currentState.setErrorMessage("");
        _state.setValue(currentState);
    }

    private void validateDates() {
        PaymentState currentState = getCurrentState();

        if (!currentState.areDatesSelected()) {
            currentState.setDatesValid(false);
            return;
        }

        try {
            Date startDate = dateFormat.parse(currentState.getStartDate());
            Date endDate = dateFormat.parse(currentState.getEndDate());

            // Check if start date is not in the past
            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(Calendar.MINUTE, 0);
            todayCalendar.set(Calendar.SECOND, 0);
            todayCalendar.set(Calendar.MILLISECOND, 0);

            if (startDate.before(todayCalendar.getTime())) {
                currentState.setDatesValid(false);
                return;
            }

            // Check if end date is after start date
            if (!endDate.after(startDate)) {
                currentState.setDatesValid(false);
                return;
            }

            // Check if borrow period is not more than 10 days
            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            long diffInDays = (diffInMillies / (1000 * 60 * 60 * 24));

            if (diffInDays > 10) {
                currentState.setDatesValid(false);
                return;
            }

            currentState.setDatesValid(true);

        } catch (ParseException e) {
            currentState.setDatesValid(false);
        }
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.trim().length() >= 10;
    }

    // Business logic
    public void completeBorrowing() {
        PaymentState currentState = getCurrentState();

        if (!currentState.canCompleteBorrow()) {
            // Set specific error messages only when button is clicked
            if (!currentState.isContactInfoComplete()) {
                currentState.setErrorMessage("Lütfen tüm iletişim bilgilerini doldurun");
            } else if (!isValidPhone(currentState.getPhone())) {
                currentState.setErrorMessage("Geçerli bir telefon numarası girin");
            } else if (!currentState.areDatesSelected()) {
                currentState.setErrorMessage("Lütfen başlangıç ve bitiş tarihlerini seçin");
            } else if (!currentState.isDatesValid()) {
                try {
                    Date startDate = dateFormat.parse(currentState.getStartDate());
                    Date endDate = dateFormat.parse(currentState.getEndDate());

                    Calendar todayCalendar = Calendar.getInstance();
                    todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    todayCalendar.set(Calendar.MINUTE, 0);
                    todayCalendar.set(Calendar.SECOND, 0);
                    todayCalendar.set(Calendar.MILLISECOND, 0);

                    if (startDate.before(todayCalendar.getTime())) {
                        currentState.setErrorMessage("Başlangıç tarihi geçmiş olamaz");
                    } else if (!endDate.after(startDate)) {
                        currentState.setErrorMessage("Bitiş tarihi başlangıç tarihinden sonra olmalıdır");
                    } else {
                        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                        long diffInDays = (diffInMillies / (1000 * 60 * 60 * 24));
                        if (diffInDays > 10) {
                            currentState.setErrorMessage("Maksimum 10 gün ödünç alabilirsiniz");
                        }
                    }
                } catch (ParseException e) {
                    currentState.setErrorMessage("Geçersiz tarih formatı");
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

        addBorrowRecordToUser();
    }

    public void addBorrowRecordToUser() {
        PaymentState currentState = getCurrentState();
        String userId = sessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            currentState.setLoading(false);
            currentState.setErrorMessage("Kullanıcı oturumu bulunamadı");
            _state.setValue(currentState);
            return;
        }

        if (!currentState.areDatesSelected()) {
            currentState.setLoading(false);
            currentState.setErrorMessage("Lütfen tarih seçin");
            _state.setValue(currentState);
            return;
        }

        try {
            Date startDate = dateFormat.parse(currentState.getStartDate());
            Date endDate = dateFormat.parse(currentState.getEndDate());

            String contactInfo = currentState.getFullName() + ", " +
                    currentState.getPhone() + ", " +
                    currentState.getAddress() + ", " +
                    currentState.getCity() + " " +
                    currentState.getPostalCode();

            BorrowRecord borrowRecord = new BorrowRecord(
                    UUID.randomUUID().toString(),
                    currentState.getBookId(),
                    currentState.getBookTitle(),
                    startDate,
                    endDate,
                    contactInfo
            );

            bookRepository.addBorrowRecordToUser(userId, borrowRecord)
                    .addOnSuccessListener(aVoid -> {
                        // Remove listing after successful borrow record
                        removeListing();
                    })
                    .addOnFailureListener(e -> {
                        PaymentState errorState = getCurrentState();
                        errorState.setLoading(false);
                        errorState.setErrorMessage("Ödünç alma kaydı eklenirken hata oluştu");
                        _state.postValue(errorState);
                    });

        } catch (ParseException e) {
            currentState.setLoading(false);
            currentState.setErrorMessage("Tarih formatı hatalı");
            _state.setValue(currentState);
        }
    }

    private void removeListing() {
        PaymentState currentState = getCurrentState();
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
                                PaymentState newState = getCurrentState();
                                newState.setLoading(false);
                                newState.setCompleted(true);
                                newState.setErrorMessage("");
                                _state.postValue(newState);
                            })
                            .addOnFailureListener(e -> {
                                PaymentState errorState = getCurrentState();
                                errorState.setLoading(false);
                                errorState.setErrorMessage("İlan kaldırılırken hata oluştu");
                                _state.postValue(errorState);
                            });
                } else {
                    PaymentState errorState = getCurrentState();
                    errorState.setLoading(false);
                    errorState.setErrorMessage("İlan sahibi bulunamadı");
                    _state.postValue(errorState);
                }
            } catch (Exception e) {
                PaymentState errorState = getCurrentState();
                errorState.setLoading(false);
                errorState.setErrorMessage("Bir hata oluştu");
                _state.postValue(errorState);
            }
        }).start();
    }

    public String calculateBorrowPeriod() {
        PaymentState currentState = getCurrentState();

        if (!currentState.areDatesSelected()) {
            return "";
        }

        try {
            Date startDate = dateFormat.parse(currentState.getStartDate());
            Date endDate = dateFormat.parse(currentState.getEndDate());

            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            long diffInDays = (diffInMillies / (1000 * 60 * 60 * 24));

            return diffInDays + " gün";

        } catch (ParseException e) {
            return "";
        }
    }

    public void clearState() {
        PaymentState currentState = getCurrentState();
        currentState.clearForm();
        _state.setValue(currentState);
    }

    private PaymentState getCurrentState() {
        PaymentState currentState = _state.getValue();
        return currentState != null ? currentState : new PaymentState();
    }
}
