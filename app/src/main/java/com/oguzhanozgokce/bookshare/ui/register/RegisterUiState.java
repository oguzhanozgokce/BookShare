package com.oguzhanozgokce.bookshare.ui.register;

public class RegisterUiState {
    private final String name;
    private final String email;
    private final String password;
    private final String errorMessage;
    private final Status status;

    public RegisterUiState(String name, String email, String password, String errorMessage, Status status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    public static RegisterUiState initial() {
        return new RegisterUiState("", "", "", null, Status.IDLE);
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public RegisterUiState withStatus(Status status) {
        return new RegisterUiState(name, email, password, errorMessage, status);
    }

    public RegisterUiState withError(String errorMessage) {
        return new RegisterUiState(name, email, password, errorMessage, Status.ERROR);
    }

    public RegisterUiState withLoading() {
        return new RegisterUiState(name, email, password, errorMessage, Status.LOADING);
    }

    public RegisterUiState success() {
        return new RegisterUiState(name, email, password, null, Status.SUCCESS);
    }
}

