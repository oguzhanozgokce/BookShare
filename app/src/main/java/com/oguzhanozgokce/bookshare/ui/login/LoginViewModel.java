package com.oguzhanozgokce.bookshare.ui.login;

import com.oguzhanozgokce.bookshare.common.BaseViewModel;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends BaseViewModel<LoginState> {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

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
}
