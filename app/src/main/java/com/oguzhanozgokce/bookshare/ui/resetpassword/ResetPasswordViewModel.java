package com.oguzhanozgokce.bookshare.ui.resetpassword;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ResetPasswordViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<ResetUiState> uiState =
            new MutableLiveData<>(ResetUiState.initial());

    public LiveData<ResetUiState> getUiState() { return uiState; }

    @Inject
    public ResetPasswordViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void onEmailChanged(String email) {
        uiState.setValue(Objects.requireNonNull(uiState.getValue()).withEmail(email));
    }

    public void sendResetLink() {
        ResetUiState current = uiState.getValue();
        if (current == null) current = ResetUiState.initial();

        String email = current.getEmail();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState.setValue(current.error("Geçersiz e‑posta adresi"));
            return;
        }

        uiState.setValue(current.loading());

        ioExecutor.execute(() -> {
            Result<String, FirebaseError> result = authRepository.resetPassword(email);

            result.fold(
                    userId  -> uiState.postValue(uiState.getValue().success()),
                    failure -> uiState.postValue(uiState.getValue().error(failure != null ? failure : "Bilinmeyen hata"))
            );
        });
    }

    @Override public void onCleared() {
        ioExecutor.shutdown();
        super.onCleared();
    }
}
