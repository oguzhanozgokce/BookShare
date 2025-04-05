package com.oguzhanozgokce.bookshare.ui.login;

public class LoginState {
    private final String email;
    private final String password;
    private final String errorMessage;
    private final Status status;

    public LoginState(String email, String password, String errorMessage, Status status) {
        this.email = email;
        this.password = password;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    public static LoginState initial() {
        return new LoginState("", "", null, Status.IDLE);
    }

    public static LoginState initial(String email, String password) {
        return new LoginState(email, password, null, Status.IDLE);
    }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getErrorMessage() { return errorMessage;}

    public Status getStatus() { return status; }

    public boolean isLoading() { return status == Status.LOADING; }

    public boolean isError() { return status == Status.ERROR; }

    public LoginState withEmailAndPassword(String email, String password) {
        return new LoginState(email, password, null, Status.IDLE);
    }

    public LoginState withError(String errorMessage) {
        return new LoginState(email, password, errorMessage, Status.ERROR);
    }

    public LoginState withLoading() {
        return new LoginState(email, password, errorMessage, Status.LOADING);
    }

    public LoginState success() {
        return new LoginState(email, password, null, Status.SUCCESS);
    }
}


