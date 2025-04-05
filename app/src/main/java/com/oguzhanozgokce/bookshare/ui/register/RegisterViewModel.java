package com.oguzhanozgokce.bookshare.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.Objects;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RegisterViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<RegisterUiState> _uiState = new MutableLiveData<>(RegisterUiState.initial());
    public LiveData<RegisterUiState> getUiState() {
        return _uiState;
    }

    @Inject
    public RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void registerUser(String name, String email, String password) {
        _uiState.setValue(Objects.requireNonNull(_uiState.getValue()).withLoading());
        Executors.newSingleThreadExecutor().execute(() -> {
            Result<String, FirebaseError> result = authRepository.registerUser(name, email, password);
            result.fold(
                    userId -> _uiState.postValue(_uiState.getValue().success()),
                    errorMessage -> _uiState.postValue(_uiState.getValue().withError(errorMessage))
            );
        });
    }
}

