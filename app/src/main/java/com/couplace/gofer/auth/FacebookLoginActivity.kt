package com.couplace.gofer.auth

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.couplace.gofer.R
import com.couplace.gofer.product.ProductActivity
import com.couplace.gofer.utils.Utils
import com.couplace.gofer.utils.Utils.saveLoginGooglePref
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.*


class FacebookLoginActivity : AppCompatActivity(), AccessToken.AccessTokenRefreshCallback, View.OnClickListener  {
    private lateinit var signin_google_button: Button
    private lateinit var facebook_login_button: LoginButton
    private var callbackManager: CallbackManager?=null
    private val EMAIL = "email"
    private val FACEBOOK_PHONE_PERMISSION = "user_mobile_phone"
    private val FACEBOOK_USER_FRIENDS_PERMISSION = "user_friends"
    private val FACEBOOK_USER_PHOTOS_PERMISSION = "user_photos"
    private val REQUEST_PERMISSION_INTERNET_CODE = 5151

    private val TAG = "FacebookLoginActivity"
    private val KEY_IS_RESOLVING = "is_resolving"
    private val KEY_CREDENTIAL = "key_credential"

    private val RC_SIGN_IN = 1
    private val RC_CREDENTIALS_READ = 2
    private val RC_CREDENTIALS_SAVE = 3

    private var mCredentialsClient: CredentialsClient? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private var mIsResolving = false
    private var mCredential: Credential? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_facebook)

        facebook_login_button = findViewById<View>(R.id.facebook_login_button) as LoginButton
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        facebook_login_button.setReadPermissions(Arrays.asList(EMAIL,FACEBOOK_PHONE_PERMISSION,FACEBOOK_USER_FRIENDS_PERMISSION,FACEBOOK_USER_PHOTOS_PERMISSION))
        facebook_login_button.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {

                Utils.saveLoginFacebookPref(this@FacebookLoginActivity, loginResult!!.accessToken.token)

                // Refresh token cached on device after login succeeds
                AccessToken.refreshCurrentAccessTokenAsync(this@FacebookLoginActivity)
                // App
                val intent = Intent(applicationContext, ProductActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onCancel() {
                // App code
                LoginManager.getInstance().logOut()
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })


        signin_google_button = findViewById<View>(R.id.signin_google_button) as Button
        signin_google_button.setOnClickListener(View.OnClickListener {
            if (!checkPermissions()) {
                requestInternetPermissions()
            }

            if (savedInstanceState != null) {
                mIsResolving =
                    savedInstanceState.getBoolean(KEY_IS_RESOLVING, false)
                mCredential = savedInstanceState.getParcelable(KEY_CREDENTIAL)
            }

            requestCredentials(true, true)
        })

    }




    fun isFacebookSessionOpened(): Boolean {
        return AccessToken.getCurrentAccessToken() != null && AccessToken.isCurrentAccessTokenActive() && !AccessToken.getCurrentAccessToken()!!.permissions.isEmpty()
    }

    override fun OnTokenRefreshFailed(exception: FacebookException?) {
        TODO("Not yet implemented")
    }

    override fun OnTokenRefreshed(accessToken: AccessToken?) {
        if (accessToken == null) {
            AccessToken.refreshCurrentAccessTokenAsync(this@FacebookLoginActivity)

        }
        if(!AccessToken.isCurrentAccessTokenActive() || AccessToken.getCurrentAccessToken()!!.permissions.isEmpty()){
            logout()
        }
        Utils.saveLoginFacebookPref(this@FacebookLoginActivity, accessToken!!.token)
    }


    companion object {
        fun logout() {
            return LoginManager.getInstance().logOut()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_RESOLVING, mIsResolving)
        outState.putParcelable(KEY_CREDENTIAL, mCredential)
    }

    override fun onStart() {
        super.onStart()
        if (!mIsResolving) {
            requestCredentials(true /* shouldResolve */, false /* onlyPasswords */)
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.signin_google_button -> requestCredentials(true, true)
            else -> {}
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignIn(task)
        } else if (requestCode == RC_CREDENTIALS_READ) {
            mIsResolving = false
            if (resultCode == RESULT_OK) {
                val credential = data!!.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                handleCredential(credential!!)
            }
        } else if (requestCode == RC_CREDENTIALS_SAVE) {
            mIsResolving = false
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            }
        } else Toast.makeText(this, "Check connection, tray again.", Toast.LENGTH_SHORT).show()
    }


    private fun requestCredentials(shouldResolve: Boolean, onlyPasswords: Boolean) {
        val crBuilder = CredentialRequest.Builder()
            .setPasswordLoginSupported(true)
        if (!onlyPasswords) {
            crBuilder.setAccountTypes(IdentityProviders.GOOGLE)
        }
        mCredentialsClient!!.request(crBuilder.build()).addOnCompleteListener(
            OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Auto sign-in success
                    handleCredential(task.result.credential!!)
                    return@OnCompleteListener
                }
                val e = task.exception
                if (e is ResolvableApiException && shouldResolve) {
                    // Getting credential needs to show some UI, start resolution
                    resolveResult(e, RC_CREDENTIALS_READ)
                }
            })
    }

    private fun resolveResult(rae: ResolvableApiException, requestCode: Int) {
        if (!mIsResolving) {
            try {
                rae.startResolutionForResult(this, requestCode)
                mIsResolving = true
            } catch (e: SendIntentException) {
                mIsResolving = false
                e.printStackTrace()
            }
        }
    }

    private fun handleCredential(credential: Credential) {
        mCredential = credential
        if (IdentityProviders.GOOGLE == credential.accountType) {
            // Google account, rebuild GoogleApiClient to set account name and then try
            buildClients(credential.id)
            googleSilentSignIn()
        } else {
            // Email/password account
        }
    }


    private fun buildClients(accountName: String?) {
        // [START config_signin]
        // Configure Google Sign In

        //OJO Si no funciona, por favor generar un nuevo proyecto en Firebase porque Google Cloud bloqueo mi cuenta por vivir en Cuba, y no puedo acceder a la consola y generar un web client token
        val gsoBuilder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_token))
            .requestEmail()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gsoBuilder.build())
        if (accountName != null) {
            gsoBuilder.setAccountName(accountName)
        }
        mCredentialsClient = Credentials.getClient(this)
        mGoogleSignInClient = GoogleSignIn.getClient(this, gsoBuilder.build())
    }

    private fun googleSilentSignIn() {
        // Try silent sign-in with Google Sign In API
        val silentSignIn = mGoogleSignInClient!!.silentSignIn()
        if (silentSignIn.isComplete && silentSignIn.isSuccessful) {
            handleGoogleSignIn(silentSignIn)
            return
        }
        silentSignIn.addOnCompleteListener { task -> handleGoogleSignIn(task) }
    }

    private fun handleGoogleSignIn(completedTask: Task<GoogleSignInAccount>?) {
        val isSignedIn = completedTask != null && completedTask.isSuccessful
        if (isSignedIn) {
            // Display signed-in UI
            try {
                val account =
                    completedTask!!.getResult(ApiException::class.java) as GoogleSignInAccount
                // Save Google Sign In to SmartLock
                val credential = Credential.Builder(
                    account.email!!
                )
                    .setAccountType(IdentityProviders.GOOGLE)
                    .setName(account.displayName!!)
                    .setProfilePictureUri(account.photoUrl!!)
                    .build()
                saveCredential(credential)
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }


    fun saveCredential(credential: Credential?) {
        if (credential == null) {
            return
        }
        mCredentialsClient!!.save(mCredential!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveLoginGooglePref(
                    this,
                    credential.id
                )
                val intent = Intent(this, ProductActivity::class.java)
                startActivity(intent)
                finish()
            }
            val e = task.exception
            if (e is ResolvableApiException) {
                // Saving the credential can sometimes require showing some UI
                // to the user, which means we need to fire this resolution.
                resolveResult(e, RC_CREDENTIALS_SAVE)
            } else {
                Toast.makeText(
                    this,
                    "Unexpected error, see logs for detals",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    /**
         * Return the current state of the permissions needed.
         */
        private fun checkPermissions(): Boolean {
            val internetPermissionState = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET
            )
            return internetPermissionState == PackageManager.PERMISSION_GRANTED
        }

        fun requestInternetPermissions() {
            val permissionAccessInternetApproved = (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET
            )
                    == PackageManager.PERMISSION_GRANTED)


            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (permissionAccessInternetApproved) {
                AlertDialog.Builder(this)
                    .setMessage("Permiso de internet para la aplicación")
                    .setPositiveButton(
                        getResources().getString(android.R.string.yes),
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            ActivityCompat.requestPermissions(
                                this, arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ), REQUEST_PERMISSION_INTERNET_CODE
                            )
                        })
                    .create().show()
            } else {
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_PERMISSION_INTERNET_CODE
                )
            }
        }

        /**
         * Callback received when a permissions request has been completed.
         */
        override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String?>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_PERMISSION_INTERNET_CODE) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.INTERNET
                    )
                ) {
                    Toast.makeText(this, "Permiso denegado por el usuario", Toast.LENGTH_SHORT)
                        .show()
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.INTERNET
                    )
                ) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }


    }