package com.oguzhanozgokce.bookshare.domain.error;

public enum FirebaseError implements Error {
    AUTH_USER_NOT_FOUND("User not found"),
    AUTH_EMAIL_ALREADY_IN_USE("Email already in use"),
    AUTH_INVALID_CREDENTIALS("Invalid credentials"),

    FIRESTORE_DOCUMENT_NOT_FOUND("Document not found"),
    FIRESTORE_PERMISSION_DENIED("Permission denied"),

    NETWORK_REQUEST_FAILED("Network request failed"),
    INVALID_DATA("Invalid data"),
    UNKNOWN_ERROR("Unknown error");


    private final String message;

    FirebaseError(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
