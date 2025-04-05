package com.oguzhanozgokce.bookshare.di;

import com.oguzhanozgokce.bookshare.data.repository.AuthRepositoryImpl;
import com.oguzhanozgokce.bookshare.data.repository.BookRepositoryImpl;
import com.oguzhanozgokce.bookshare.domain.AuthRepository;
import com.oguzhanozgokce.bookshare.domain.BookRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract BookRepository bindBookRepository(BookRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract AuthRepository bindUserRepository(AuthRepositoryImpl impl);
}