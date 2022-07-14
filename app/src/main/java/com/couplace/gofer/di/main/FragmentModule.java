package com.couplace.gofer.di.main;

import com.couplace.gofer.favoriteproducts.FavoriteProductsFragment;
import com.couplace.gofer.product.ProductActivity;
import com.couplace.gofer.profile.ProfileFragment;
import com.couplace.gofer.setting.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract FavoriteProductsFragment contributeFavoriteProductsFragment();

    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();


    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();
}
