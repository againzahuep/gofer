package com.couplace.gofer.di.module

import androidx.lifecycle.ViewModelProvider
import com.couplace.gofer.viewmodel.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory:ViewModelProviderFactory?): ViewModelProvider.Factory?

}