package com.couplace.gofer.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.couplace.gofer.GoferApplication
import com.couplace.gofer.R
import com.couplace.gofer.di.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(goferApplication: GoferApplication) {

    private var mApplication: Application? = null

    fun AppModule(app: Application?) {
        mApplication = app
    }

    @Provides
    @ApplicationContext
    fun provideContext(): Context? {
        return mApplication
    }

    @Provides
    fun provideApplication(): Application? {
        return mApplication
    }


    @Provides
    fun provideSharedPrefs(): SharedPreferences? {
        return mApplication!!.getSharedPreferences("gofer-prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions? {
        return RequestOptions
            .placeholderOf(R.drawable.white_background)
            .error(R.drawable.white_background)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        application: Application?,
        requestOptions: RequestOptions?
    ): RequestManager? {
        return Glide.with(application!!)
            .setDefaultRequestOptions(requestOptions!!)
    }

    @Singleton
    @Provides
    fun provideAppDrawable(application: Application?): Drawable? {
        return ContextCompat.getDrawable(application!!, R.drawable.agence)
    }

}