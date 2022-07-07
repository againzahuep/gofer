package com.couplace.gofer.di.main;

import com.couplace.gofer.product.ProductListAdapter;
import com.couplace.gofer.product.ProductListAdapter.Interaction;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    @MainScope
    @Provides
    static ProductListAdapter provideAdapter(Interaction interaction){
        return new ProductListAdapter(interaction);
    }


}
