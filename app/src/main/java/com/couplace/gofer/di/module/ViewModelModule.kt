package com.couplace.gofer.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.couplace.gofer.product.ProductViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds abstract fun bindsViewModelFactory(factory: MyViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel::class)
    abstract fun bindProductsViewModel(viewModel: ProductViewModel?): ViewModel?


}