package com.couplace.gofer.di.module

import com.couplace.gofer.auth.FacebookLoginActivity
import com.couplace.gofer.auth.GoogleSignInActivity
import com.couplace.gofer.di.main.MainFragmentBuildersModule
import com.couplace.gofer.di.main.MainModule
import com.couplace.gofer.di.main.MainScope
import com.couplace.gofer.di.main.MainViewModelsModule
import com.couplace.gofer.product.ProductActivity
import com.couplace.gofer.productdetails.ProductDetailsActivity
import com.couplace.gofer.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuildersModule {

    @MainScope
    @ContributesAndroidInjector(modules = [MainFragmentBuildersModule::class, MainViewModelsModule::class, MainModule::class])

    abstract fun contributeSplashActivity(): SplashActivity?
    abstract fun contributeGoogleSignInActivity(): GoogleSignInActivity?
    abstract fun contributeFacebookActivity(): FacebookLoginActivity?
    abstract fun contributeProductActivity(): ProductActivity?
    abstract fun contributeProductDetailsActivity(): ProductDetailsActivity?


}