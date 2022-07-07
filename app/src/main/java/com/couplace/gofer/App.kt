package com.couplace.gofer

import android.app.Activity
import android.app.Application
import android.app.Service
import com.couplace.gofer.di.component.AppComponent
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.android.AndroidInjector

import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import timber.log.Timber

import java.util.*
import javax.inject.Inject

class App : Application(), HasActivityInjector, HasServiceInjector{
    private lateinit var appComponent: AppComponent
    var LOG = Timber.tag(this::class.java.simpleName)

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun activityInjector(): AndroidInjector<Activity> = androidInjector

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector

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


