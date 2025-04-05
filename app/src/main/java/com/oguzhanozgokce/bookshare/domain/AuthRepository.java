package com.oguzhanozgokce.bookshare.domain;

import com.oguzhanozgokce.bookshare.common.Result;
import com.oguzhanozgokce.bookshare.domain.error.FirebaseError;

public interface AuthRepository {
    Result<String, FirebaseError> registerUser(String name, String email, String password);
    Result<String, FirebaseError> loginUser(String email, String password);
    String getCurrentUserId();
}
