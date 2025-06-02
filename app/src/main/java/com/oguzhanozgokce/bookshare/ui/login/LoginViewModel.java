package com.oguzhanozgokce.bookshare.ui.login;

import com.oguzhanozgokce.bookshare.common.BaseViewModel;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends BaseViewModel<LoginState> {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    protected LoginState initialState() {
        return LoginState.initial();
    }

    @Inject
    public LoginViewModel(AuthRepository authRepository, SessionManager sessionManager) {
        this.authRepository = authRepository;
        this.sessionManager = sessionManager;
        loadSavedCredentials();
    }

    private void loadSavedCredentials() {
        String email = sessionManager.getEmail();
        String password = sessionManager.getPassword();
        updateState(state -> state.withEmailAndPassword(email, password));
    }

    public void login(String email, String password) {
        // Input validasyonları
        String validationError = validateLoginInputs(email, password);
        if (validationError != null) {
            updateState(state -> state.withError(validationError));
            return;
        }

        updateState(LoginState::withLoading);
        Executors.newSingleThreadExecutor().execute(() -> {
            Result<String, FirebaseError> result = authRepository.loginUser(email, password);
            if (result.isSuccess()) {
                updateState(LoginState::success);
            } else {
                FirebaseError error = result.getError();
                updateState(state -> state.withError(error.getMessage()));
            }
        });
    }

    private String validateLoginInputs(String email, String password) {
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
        if (password.trim().length() < 6) {
            return "Şifre en az 6 karakter olmalıdır";
        }

        return null; 
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
