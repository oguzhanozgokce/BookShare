package com.oguzhanozgokce.bookshare.domain.error;

public enum FirebaseError implements Error {
    AUTH_USER_NOT_FOUND("User not found", "ERROR_USER_NOT_FOUND"),
    AUTH_EMAIL_ALREADY_IN_USE("Email already in use", "ERROR_EMAIL_ALREADY_IN_USE"),
    AUTH_INVALID_CREDENTIALS("Invalid credentials", "ERROR_INVALID_CREDENTIAL"),

    FIRESTORE_DOCUMENT_NOT_FOUND("Document not found", "NOT_FOUND"),
    FIRESTORE_PERMISSION_DENIED("Permission denied", "PERMISSION_DENIED"),

    NETWORK_REQUEST_FAILED("Network request failed", "ERROR_NETWORK_REQUEST_FAILED"),
    INVALID_DATA("Invalid data", "INVALID_DATA"),
    UNKNOWN_ERROR("Unknown error", "UNKNOWN_ERROR");

    private final String message;
    private final String code;

    FirebaseError(String message, String code) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public static FirebaseError fromCode(String code) {
        for (FirebaseError error : values()) {
            if (error.code.equalsIgnoreCase(code)) {
                return error;
            }
        }
        return UNKNOWN_ERROR;
    }
}
