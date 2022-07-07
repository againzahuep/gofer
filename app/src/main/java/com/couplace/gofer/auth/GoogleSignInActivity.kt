package com.couplace.gofer.auth


import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.couplace.gofer.R
import com.couplace.gofer.product.ProductActivity
import com.couplace.gofer.utils.Utils
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber


class GoogleSignInActivity : AppCompatActivity() {

    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient
    private lateinit var mAuth: FirebaseAuth
    private val REQ_ONE_TAP: Int = 0


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        var currentUser = mAuth.getCurrentUser()

        if (currentUser!=null){
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance();

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    //Si no funciona, generar un nuevo proyecto en firebase porque Google BLOCK my account y un web client token y colocarlo en strings.xml file
                    .setServerClientId(getString(R.string.google_web_client_token))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.

            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    e.message
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQ_ONE_TAP){
            val googleCredential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = googleCredential.googleIdToken
            if (idToken != null) {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                        override fun onComplete(@NonNull task: Task<AuthResult?>) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Timber.d("signInWithCredential:success")
                                val user = mAuth.currentUser
                                Utils.saveLoginGooglePref(this@GoogleSignInActivity, idToken)

                                val intent = Intent(applicationContext, ProductActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                // If sign in fails, display a message to the user.
                                Timber.w(task.getException(), "signInWithCredential:failure")
                            }
                        }
                    })
            }

        }
    }

}