package com.couplace.gofer.di.component

import android.app.Application
import com.couplace.gofer.App
import com.couplace.gofer.auth.FacebookLoginActivity
import com.couplace.gofer.auth.GoogleSignInActivity
import com.couplace.gofer.di.module.AppModule
import com.couplace.gofer.di.module.ActivityBuildersModule
import com.couplace.gofer.di.module.RepositoryModule
import com.couplace.gofer.di.module.ViewModelFactoryModule
import com.couplace.gofer.product.ProductActivity
import com.couplace.gofer.productdetails.ProductDetailsActivity
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        (AppModule::class),
        (AndroidInjectionModule::class),
        (ActivityBuildersModule::class),
        (ViewModelFactoryModule::class),
        (ActivityBuildersModule::class),
        (RepositoryModule::class)

    ]
)

interface AppComponent: AndroidInjector<App> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(productActivity: ProductActivity)

    fun inject(productDetailsActivity: ProductDetailsActivity)

    fun inject(facebookLoginActivity: FacebookLoginActivity)

    fun inject(googleSignInActivity: GoogleSignInActivity)

}