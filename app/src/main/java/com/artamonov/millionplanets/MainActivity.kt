package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.data.AppPreferenceHelper
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private var googleApiClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 7
    private val appPreferenceHelper: AppPreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)
//        if (firebaseAuth.currentUser != null) {
//            continue_btn.visibility = View.VISIBLE
//        }

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            continue_btn.visibility = View.VISIBLE
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleSignIn.getClient(this, gso)

        google_sign_in.setOnClickListener {
            signIn()
        }

        continue_btn.setOnClickListener {
            goToMainOptions()
        }

        start.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun signIn() {
        val signInIntent = googleApiClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

//    fun onNewGame(view: View) {
//        val intent = Intent(this, RegistrationActivity::class.java)
//        startActivity(intent)
//    }

    private fun goToMainOptions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(
                    Intent(applicationContext, MainOptionsActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                //  appPreferenceHelper?.saveUserProfile(account)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Login", "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        if (firebaseAuth.currentUser?.displayName == null) {
                            startActivity(
                                    Intent(
                                            applicationContext,
                                            NewGameActivity::class.java))
                        } else {
                            goToMainOptions()
                        }
//                        val profileUpdates = UserProfileChangeRequest.Builder()
//                                .setDisplayName("User234")
//                                .build()
//                        firebaseAuth.currentUser?.updateProfile(profileUpdates)
//                                ?.addOnCompleteListener { task ->
//                                    if (task.isSuccessful) {
//                                        goToMainOptions()
//                                    } else {
//
//                                    }
//                                }
                        //   updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        //   Log.w(TAG, "signInWithCredential:failure", task.exception)
                            Snackbar.make(findViewById(android.R.id.content), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                        // updateUI(null)
                    }

                    // ...
                }
    }
}