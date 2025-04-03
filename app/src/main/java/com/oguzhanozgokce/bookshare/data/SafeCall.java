package com.oguzhanozgokce.bookshare.data;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

import java.util.concurrent.Callable;

public class SafeCall {
    public static <T> Result<T, FirebaseError> safeCall(Callable<T> operation) {
        try {
            T data = operation.call();
            return Result.success(data);

        } catch (FirebaseAuthException e) {
            return mapAuthException(e);

        } catch (FirebaseFirestoreException e) {
            return mapFirestoreException(e);

        } catch (Exception e) {
            return Result.error(FirebaseError.UNKNOWN_ERROR);
        }
    }

    private static <T> Result<T, FirebaseError> mapAuthException(FirebaseAuthException e) {
        switch (e.getErrorCode()) {
            case "ERROR_USER_NOT_FOUND":
                return Result.error(FirebaseError.AUTH_USER_NOT_FOUND);
            case "ERROR_EMAIL_ALREADY_IN_USE":
                return Result.error(FirebaseError.AUTH_EMAIL_ALREADY_IN_USE);
            case "ERROR_NETWORK_REQUEST_FAILED":
                return Result.error(FirebaseError.NETWORK_REQUEST_FAILED);
            default:
                return Result.error(FirebaseError.AUTH_INVALID_CREDENTIALS);
        }
    }

    private static <T> Result<T, FirebaseError> mapFirestoreException(FirebaseFirestoreException e) {
        switch (e.getCode()) {
            case NOT_FOUND:
                return Result.error(FirebaseError.FIRESTORE_DOCUMENT_NOT_FOUND);
            case PERMISSION_DENIED:
                return Result.error(FirebaseError.FIRESTORE_PERMISSION_DENIED);
            default:
                return Result.error(FirebaseError.UNKNOWN_ERROR);
        }
    }
}
