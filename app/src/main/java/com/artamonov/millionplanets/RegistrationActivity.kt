package com.artamonov.millionplanets

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.registration.*
import java.util.HashMap

class RegistrationActivity : BaseActivity() {
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)
        progressDialog = ProgressDialog(this)

        register_login_firebase.setOnClickListener {

            val email = register_email.text.toString()
            val password = register_password.text.toString()
            val username = register_username.text.toString()

            if (!nicknameIsValid()) {
                Snackbar.make(findViewById(android.R.id.content), "Your nickname must be at least 3 characters", Snackbar.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            if (!passwordIsValid()) {
                Snackbar.make(findViewById(android.R.id.content), "Your password must be at least 6 characters", Snackbar.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Snackbar.make(findViewById(android.R.id.content), "Enter your email to continue registration", Snackbar.LENGTH_SHORT)
                        .show()
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
                            Toast.makeText(
                                    applicationContext,
                                    "The email is already occupied",
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }

                        if (task.exception is FirebaseAuthWeakPasswordException) {
                        showProgressBar(false)
                        Toast.makeText(
                                applicationContext,
                                "The password must be at least 6 characters",
                                Toast.LENGTH_SHORT)
                                .show()
                    }

                        if (task.exception is FirebaseAuthEmailException) {
                            showProgressBar(false)
                            Toast.makeText(
                                    applicationContext,
                                    "The email is invalid",
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }

                        if (task.exception is Any) {
                            showProgressBar(false)
                            Toast.makeText(
                                    applicationContext,
                                    task.exception!!.message,
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }

                        if (task.isSuccessful) {
                            setUpUserProfile(username)
                        }
                    }
        }
    }

    private fun passwordIsValid(): Boolean {
        return register_password.text.length > 5
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
        val user = User()
        user.x = 5
        user.y = 6
        user.cargo = 10
        user.hp = 50
        user.ship = "Fighter"
        user.money = 1000
        user.scanner_capacity = 15
        user.shield = 100
        user.jump = 10
        user.fuel = 20
        user.nickname = username
        user.email = firebaseUser?.email
        user.type = "user"
        user.sumXY = 11

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
                                    Snackbar.make(findViewById(android.R.id.content), "Registration is failed. Please, try later.", Snackbar.LENGTH_SHORT)
                                            .show()
                                }
                            }
                }
    }

    private fun nicknameIsValid(): Boolean {
        return register_username.text.length > 2
    }
}
