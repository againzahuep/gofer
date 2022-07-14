package com.couplace.gofer

import android.content.Context
import com.couplace.gofer.di.component.ApplicationComponent
import com.couplace.gofer.di.component.DaggerAppComponent
import com.couplace.gofer.di.module.AppModule
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.android.*

import timber.log.Timber

import java.util.*

class GoferApplication : DaggerApplication(){
    protected var applicationComponent: ApplicationComponent? = null

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        applicationComponent = DaggerAppComponent.builder().applicationModule(AppModule(this)).build()
        applicationComponent!!.inject(this)

        return DaggerAppComponent.builder().build()
    }

    operator fun get(context: Context): GoferApplication {
        return context.applicationContext as GoferApplication
    }

    fun getComponent(): ApplicationComponent? {
        return applicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AppInjector.init(this)
        AppEventsLogger.activateApp(this)
        Fresco.initialize(this)
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.setAutoLogAppEventsEnabled(false)
    }

}


