package com.oguzhanozgokce.bookshare.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Function;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<LoginState> _loginState = new MutableLiveData<>(LoginState.initial());
    public LiveData<LoginState> getLoginState() {
        return _loginState;
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
        _loginState.setValue(LoginState.initial(email, password));
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

    private void updateState(Function<LoginState, LoginState> updateFn) {
        LoginState current = _loginState.getValue();
        if (current != null) {
            _loginState.postValue(updateFn.apply(current));
        }
    }
}
