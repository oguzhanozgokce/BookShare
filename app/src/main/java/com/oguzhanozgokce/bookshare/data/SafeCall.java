package com.oguzhanozgokce.bookshare.data;

import android.util.Log;

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
            return mapFireStoreException(e);

        } catch (Exception e) {
            Log.e("SafeCall", "An error occurred: ", e);
            return Result.error(FirebaseError.UNKNOWN_ERROR);
        }
    }

    private static <T> Result<T, FirebaseError> mapAuthException(FirebaseAuthException e) {
        FirebaseError error = FirebaseError.fromCode(e.getErrorCode());
        return Result.error(error);
    }

    private static <T> Result<T, FirebaseError> mapFireStoreException(FirebaseFirestoreException e) {
        FirebaseError error = FirebaseError.fromCode(e.getCode().name());
        return Result.error(error);
    }
}
