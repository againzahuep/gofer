package com.couplace.gofer.di.module

import android.app.Activity
import android.content.Context
import com.couplace.gofer.auth.FacebookLoginActivity
import com.couplace.gofer.auth.GoogleSignInActivity
import com.couplace.gofer.di.ActivityContext
import com.couplace.gofer.di.main.FragmentModule
import com.couplace.gofer.product.ProductActivity
import com.couplace.gofer.productdetails.ProductDetailsActivity
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityModule {

    private var mActivity: Activity? = null

    open fun ActivityModule(activity: Activity) {
        mActivity = activity
    }

    @Provides
    @ActivityContext
    open fun provideContext(): Context? {
        return mActivity
    }

    @Provides
    open fun provideActivity(): Activity? {
        return mActivity
    }


    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun bindProductActivity(): ProductActivity?

    abstract fun bindGoogleSignInActivity(): GoogleSignInActivity?
    abstract fun bindFacebookLoginActivity(): FacebookLoginActivity?
    abstract fun bindProductDetailsActivity(): ProductDetailsActivity?


}