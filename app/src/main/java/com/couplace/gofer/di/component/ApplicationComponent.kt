package com.couplace.gofer.di.component

import android.app.Application
import android.content.Context
import com.couplace.gofer.GoferApplication
import com.couplace.gofer.di.ApplicationContext
import com.couplace.gofer.di.main.MainModule
import com.couplace.gofer.di.module.ActivityModule
import com.couplace.gofer.di.module.AppModule
import com.couplace.gofer.di.module.RepositoryModule
import com.couplace.gofer.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        (AndroidSupportInjectionModule::class),
        (AppModule::class),
        (ActivityModule::class),
        (ViewModelModule::class),
        (ActivityModule::class),
        (RepositoryModule::class),
        (MainModule::class)

    ]
)

interface ApplicationComponent: AndroidInjector<GoferApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    override fun inject(goferApplication: GoferApplication?)

    @ApplicationContext
    fun getContext(): Context?

    fun getApplication(): Application?



}