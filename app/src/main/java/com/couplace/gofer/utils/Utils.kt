/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.couplace.gofer.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.afollestad.materialdialogs.MaterialDialog
import com.couplace.gofer.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Returns the `location` object as a human readable string.
 */
fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

private var toast: Toast? = null

fun Activity.toast(message: CharSequence) {
    toast?.cancel()
    toast = Toast.makeText(this, message, Toast.LENGTH_SHORT).apply { show() }
}

/**
 * Attach the dialog to a lifecycle and dismiss it when the lifecycle is destroyed.
 * Uses the given [owner] lifecycle if provided, else falls back to the Context of the dialog
 * window if it can.
 *
 * @param owner Optional lifecycle owner, if its null use windowContext.
 */
fun MaterialDialog.lifecycleOwner(owner: LifecycleOwner? = null): MaterialDialog {
    val observer = DialogLifecycleObserver(::dismiss)
    val lifecycleOwner = owner ?: (windowContext as? LifecycleOwner
        ?: throw IllegalStateException(
            "$windowContext is not a LifecycleOwner."
        ))
    lifecycleOwner.lifecycle.addObserver(observer)
    return this
}

internal class DialogLifecycleObserver(private val dismiss: () -> Unit) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() = dismiss()
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() = dismiss()
}


typealias PrefEditor = SharedPreferences.Editor

internal fun SharedPreferences.boolean(
    key: String,
    defaultValue: Boolean = false
): Boolean {
    return getBoolean(key, defaultValue)
}

internal inline fun SharedPreferences.commit(crossinline exec: PrefEditor.() -> Unit) {
    val editor = this.edit()
    editor.exec()
    editor.apply()
}

internal fun drawableToBitmapDescriptor(drawable: Drawable): BitmapDescriptor? {
    val bitmap: Bitmap
    if (drawable is BitmapDrawable) {
        val bitmapDrawable = drawable
        if (bitmapDrawable.bitmap != null) {
            return BitmapDescriptorFactory.fromBitmap(bitmapDrawable.bitmap)
        }
    }
    bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/**
 * Provides access to SharedPreferences for location to Activities and Services.
 */
internal object Utils {

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */


        val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"

        val KEY_LOGIN_FACEBOOK = "facebook"

        val KEY_LOGIN_GOOGLE = "google"

        @JvmStatic
        fun getLocationTrackingPref(context: Context): Boolean =
            context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getBoolean(KEY_FOREGROUND_ENABLED, false)

        /**
         * Stores the location updates state in SharedPreferences.
         * @param requestingLocationUpdates The location updates state.
         */
        @JvmStatic
        fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
            context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE).edit {
                putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
            }

        ///Guardar el tipo de autenticacion si se realiza con Facebook o con Google
        @JvmStatic
        fun getLoginFacebookPref(context: Context, facebook: String): Boolean =
            context.getSharedPreferences(
                context.getString(R.string.preference_login_facebook), Context.MODE_PRIVATE)
                .getBoolean(facebook, true)

        /**
         * Stores the location updates state in SharedPreferences.
         * @param requestingLocationUpdates The location updates state.
         */
        @JvmStatic
        fun saveLoginFacebookPref(context: Context, facebook: String) =
            context.getSharedPreferences(
                context.getString(R.string.preference_login_facebook),
                Context.MODE_PRIVATE).edit {
                putString(KEY_LOGIN_FACEBOOK, facebook)
            }
        @JvmStatic
        fun getLoginGooglePref(context: Context, google: String): Boolean =
            context.getSharedPreferences(
                context.getString(R.string.preference_login_google), Context.MODE_PRIVATE)
                .getBoolean(KEY_LOGIN_GOOGLE, true)

        /**
         * Stores the location updates state in SharedPreferences.
         * @param requestingLocationUpdates The location updates state.
         */
        @JvmStatic
        fun saveLoginGooglePref(context: Context, google: String) =
            context.getSharedPreferences(
                context.getString(R.string.preference_login_google),
                Context.MODE_PRIVATE).edit {
                putString(KEY_LOGIN_GOOGLE, google)
            }


    }


