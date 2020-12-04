package com.artamonov.millionplanets

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.RegistrationActivityBinding
import com.artamonov.millionplanets.model.SpaceshipType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.HashMap

class RegistrationActivity : BaseActivity() {
    var progressDialog: ProgressDialog? = null

    lateinit var binding: RegistrationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)

        binding.registerLoginFirebase.setOnClickListener {

            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val username = binding.registerUsername.text.toString()

            if (!nicknameIsValid()) {
                showSnackbarError(getString(R.string.registration_nickname_must_at_least_3_characters_long))
                return@setOnClickListener
            }

            if (!passwordIsValid()) {
                showSnackbarError(getString(R.string.registration_password_must_at_least_6_characters_long))
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                showSnackbarError(getString(R.string.registration_enter_email))
                return@setOnClickListener
            }

            showProgressBar(true)

            firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                            this
                    ) { task ->
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            showProgressBar(false)
                            showSnackbarError(getString(R.string.registration_email_is_occupied))
                        }

                        if (task.exception is FirebaseAuthWeakPasswordException) {
                            showProgressBar(false)
                            showSnackbarError(getString(R.string.registration_password_must_at_least_6_characters_long))
                        }

                        if (task.exception is FirebaseAuthEmailException) {
                            showProgressBar(false)
                            showSnackbarError(getString(R.string.registration_email_is_invalid))
                        }

                        if (task.exception is Any) {
                            showProgressBar(false)
                            task.exception?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                            showSnackbarError(getString(R.string.error_general))
                        }

                        if (task.isSuccessful) {
                            setUpUserProfile(username)
                        }
                    }
        }
    }

    private fun passwordIsValid(): Boolean {
        return binding.registerPassword.text.length > 5
    }

    private fun showProgressBar(state: Boolean) {
        when (state) {
            true -> {
                progressDialog?.setCancelable(false)
                progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog?.show() }
            false -> {
                progressDialog?.dismiss()
            }
        }
    }

    private fun setUpUserProfile(username: String) {
        val documentReferenceInventory = firebaseFirestore.collection("Inventory").document(username)
        val documentReferenceObjects = firebaseFirestore.collection("Objects").document(username)
        val user = User(x = 5, y = 6, cargoCapacity = 10, hp = 50, ship = SpaceshipType.FIGHTER,
        money = 1000, scanner_capacity = 15, shield = 100, jump = 10, fuel = 20, nickname =
        username, email = firebaseUser?.email, type = "user", sumXY = 11)

        firebaseFirestore
                .runTransaction { transaction ->
                    transaction.set(documentReferenceObjects, user)
                    val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                    firebaseUser?.updateProfile(profileUpdates)
                    val ironData = HashMap<String, Any>()
                    ironData["Iron"] = 0
                    ironData["Mercaster"] = 0
                    ironData["Leabia"] = 0
                    ironData["Cracaphill"] = 0
                    transaction.set(documentReferenceInventory, ironData)
                    null
                }
                .addOnSuccessListener {
                    showProgressBar(false)
                    startActivity(
                            Intent(
                                    applicationContext,
                                    MainOptionsActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    firebaseUser?.delete()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showProgressBar(false)
                                    showSnackbarError(getString(R.string.registration_is_failed))
                                }
                            }
                }
    }

    private fun nicknameIsValid(): Boolean {
        return binding.registerUsername.text.length > 2
    }
}
