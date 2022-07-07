package com.couplace.gofer.di.module

import com.couplace.gofer.repository.DataRepository
import com.couplace.gofer.repository.DataRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideDataRepository(): DataRepository {
        return DataRepositoryImpl()
    }
}