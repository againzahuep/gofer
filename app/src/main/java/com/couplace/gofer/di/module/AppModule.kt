package com.couplace.gofer.di.module

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.couplace.gofer.R
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [(ViewModelFactoryModule::class)])
class AppModule {

    @Provides
    fun providersApplication(app: Application): Context = app



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