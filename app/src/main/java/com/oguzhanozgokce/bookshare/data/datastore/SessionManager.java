package com.oguzhanozgokce.bookshare.data.datastore;

import com.oguzhanozgokce.bookshare.domain.LocalDataSource;

public class SessionManager {
    private final LocalDataSource local;

    public SessionManager(LocalDataSource local) {
        this.local = local;
    }

    public void saveSession(String userId, String email, String password) {
        local.saveUserId(userId);
        local.saveEmail(email);
        local.savePassword(password);
    }

    public void clearSession() {
        local.clearAll();
    }

    public String getUserId() {
        return local.getUserId();
    }

    public String getEmail() {
        return local.getEmail();
    }

    public String getPassword() {
        return local.getPassword();
    }
}
