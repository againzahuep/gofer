package com.couplace.gofer.di.main;


import androidx.lifecycle.ViewModel;


import com.couplace.gofer.di.module.ViewModelKey;
import com.couplace.gofer.product.ProductViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelsModule {


    @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel.class)
    public abstract ViewModel bindProductsViewModel(ProductViewModel viewModel);
}




