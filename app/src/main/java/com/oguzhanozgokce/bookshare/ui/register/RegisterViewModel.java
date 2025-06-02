package com.oguzhanozgokce.bookshare.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RegisterViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<RegisterUiState> _uiState = new MutableLiveData<>(RegisterUiState.initial());
    public LiveData<RegisterUiState> getUiState() {
        return _uiState;
    }

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Name validation pattern (sadece harf, boşluk ve Türkçe karakterler)
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-ZçĞğıİöÖşŞüÜ\\s]+$"
    );

    @Inject
    public RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void registerUser(String name, String email, String password) {
        // Input validasyonları
        String validationError = validateRegisterInputs(name, email, password);
        if (validationError != null) {
            _uiState.setValue(Objects.requireNonNull(_uiState.getValue()).withError(validationError));
            return;
        }

        _uiState.setValue(Objects.requireNonNull(_uiState.getValue()).withLoading());
        Executors.newSingleThreadExecutor().execute(() -> {
            Result<String, FirebaseError> result = authRepository.registerUser(name.trim(), email.trim(), password);
            result.fold(
                    userId -> _uiState.postValue(_uiState.getValue().success()),
                    errorMessage -> _uiState.postValue(_uiState.getValue().withError(errorMessage))
            );
        });
    }

    private String validateRegisterInputs(String name, String email, String password) {
        // İsim boş kontrolü
        if (name == null || name.trim().isEmpty()) {
            return "İsim alanı boş olamaz";
        }

        // İsim minimum uzunluk kontrolü
        if (name.trim().length() < 2) {
            return "İsim en az 2 karakter olmalıdır";
        }

        // İsim maksimum uzunluk kontrolü
        if (name.trim().length() > 50) {
            return "İsim en fazla 50 karakter olmalıdır";
        }

        // İsim format kontrolü (sadece harf ve boşluk)
        if (!isValidName(name.trim())) {
            return "İsim sadece harf ve boşluk içerebilir";
        }

        // Email boş kontrolü
        if (email == null || email.trim().isEmpty()) {
            return "Email adresi boş olamaz";
        }

        // Email format kontrolü
        if (!isValidEmail(email.trim())) {
            return "Geçerli bir email adresi girin";
        }

        // Şifre boş kontrolü
        if (password == null || password.trim().isEmpty()) {
            return "Şifre boş olamaz";
        }

        // Şifre minimum uzunluk kontrolü
        if (password.length() < 6) {
            return "Şifre en az 6 karakter olmalıdır";
        }

        // Şifre maksimum uzunluk kontrolü
        if (password.length() > 128) {
            return "Şifre en fazla 128 karakter olmalıdır";
        }

        // Şifre güçlülük kontrolü
        if (!isStrongPassword(password)) {
            return "Şifre en az 1 harf ve 1 rakam içermelidir";
        }

        return null; // Hata yok
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    private boolean isStrongPassword(String password) {
        boolean hasLetter = password.matches(".*[a-zA-ZçĞğıİöÖşŞüÜ].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }
}
