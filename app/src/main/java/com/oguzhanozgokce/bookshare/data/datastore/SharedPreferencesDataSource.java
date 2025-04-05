package com.oguzhanozgokce.bookshare.data.datastore;

import android.content.Context;
import android.content.SharedPreferences;

import com.oguzhanozgokce.bookshare.domain.LocalDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPreferencesDataSource implements LocalDataSource {

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences prefs;

    @Inject
    public SharedPreferencesDataSource(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveUserId(String id) {
        prefs.edit().putString(KEY_USER_ID, id).apply();
    }

    @Override
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    @Override
    public void clearUserId() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }

    @Override
    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    @Override
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    @Override
    public void clearEmail() {
        prefs.edit().remove(KEY_EMAIL).apply();
    }

    @Override
    public void savePassword(String password) {
        prefs.edit().putString(KEY_PASSWORD, password).apply();
    }

    @Override
    public String getPassword() {
        return prefs.getString(KEY_PASSWORD, "");
    }

    @Override
    public void clearPassword() {
        prefs.edit().remove(KEY_PASSWORD).apply();
    }

    @Override
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
