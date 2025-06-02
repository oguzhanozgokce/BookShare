package com.oguzhanozgokce.bookshare.ui.resetpassword;

import android.text.TextUtils;

public final class ResetUiState {

    private final String email;
    private final boolean loading;
    private final boolean success;
    private final String errorMessage;

    private ResetUiState(String email,
                         boolean loading,
                         boolean success,
                         String errorMessage) {
        this.email = email;
        this.loading = loading;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ResetUiState initial() {
        return new ResetUiState("", false, false, null);
    }

    public ResetUiState withEmail(String email) {
        return new ResetUiState(email, false, false, null);
    }

    public ResetUiState loading() {
        return new ResetUiState(email, true, false, null);
    }

    public ResetUiState success() {
        return new ResetUiState(email, false, true, null);
    }

    public ResetUiState error(String message) {
        return new ResetUiState(email, false, false, message);
    }

    public boolean isEmailValid() { return !TextUtils.isEmpty(email); }
    public boolean isLoading()    { return loading; }
    public boolean isSuccess()    { return success; }

    public String  getEmail()        { return email; }
    public String  getErrorMessage() { return errorMessage; }
}
