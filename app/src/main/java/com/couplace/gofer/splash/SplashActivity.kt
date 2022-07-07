package com.couplace.gofer.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.couplace.gofer.R
import com.couplace.gofer.auth.FacebookLoginActivity
import com.google.android.material.navigation.NavigationView
import timber.log.Timber


class SplashActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).post {

            // Start activity
            startActivity(Intent(this, FacebookLoginActivity::class.java))

            // Animate the loading of new activity
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            // Close this activity
            finish()

        }

    }

}