package com.oguzhanozgokce.bookshare.domain;

public interface LocalDataSource {
    void saveUserId(String id);
    String getUserId();
    void clearUserId();

    void saveEmail(String email);
    String getEmail();
    void clearEmail();

    void savePassword(String password);
    String getPassword();
    void clearPassword();

    void clearAll();
}
