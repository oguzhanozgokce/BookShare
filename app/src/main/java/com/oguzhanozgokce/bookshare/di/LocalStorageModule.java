package com.oguzhanozgokce.bookshare.di;

import android.content.Context;

import com.oguzhanozgokce.bookshare.data.datastore.SharedPreferencesDataSource;
import com.oguzhanozgokce.bookshare.data.datastore.SessionManager;
import com.oguzhanozgokce.bookshare.domain.LocalDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class LocalStorageModule {

    @Provides
    @Singleton
    public static LocalDataSource provideDataSource(@ApplicationContext Context context) {
        return new SharedPreferencesDataSource(context);
    }

    @Provides
    @Singleton
    public static SessionManager provideSessionManager(LocalDataSource localDataSource) {
        return new SessionManager(localDataSource);
    }
}
