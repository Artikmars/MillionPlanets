package com.artamonov.millionplanets

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.SpaceshipType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.create_player_activity.*
import java.util.HashMap

class NewGameActivity : BaseActivity(R.layout.create_player_activity) {
    private lateinit var username: String
    private var documentReferenceInventory: DocumentReference? = null
    private var documentReferenceObjects: DocumentReference? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        go_to_space.setOnClickListener {
            onGoToSpace()
        }
    }

    private fun onGoToSpace() {
        if (!nicknameIsValid()) {
            showSnackbarError(getString(R.string.registration_nickname_must_at_least_3_characters_long))
            return
        }

        firebaseAuth = FirebaseAuth.getInstance()
        username = register_username.text.toString()
        firebaseUser = firebaseAuth.currentUser
        documentReferenceInventory = firebaseFirestore.collection("Inventory").document(username)
        documentReferenceObjects = firebaseFirestore.collection("Objects").document(username)
        user = User(x = 5, y = 6, cargoCapacity = 10, hp = 50, ship = SpaceshipType.FIGHTER,
                money = 1000, scanner_capacity = 15, shield = 100, jump = 10, fuel = 20, nickname =
        username, email = firebaseUser?.email, type = "user", sumXY = 11)

        // Log.i("myTags", "nickname: " + firebaseUser.getDisplayName());
        /*  firebaseFirestore.collection("Objects").document(username)
                        .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        firebaseUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), MainOptionsActivity.class));
                            }
                        });

                    }
                });
        */

        firebaseFirestore
                .runTransaction { transaction ->
                    transaction.set(documentReferenceObjects!!, user!!)
                    val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                    firebaseUser!!.updateProfile(profileUpdates)
                    val ironData = HashMap<String, Any>()
                    ironData["Iron"] = 0
                    ironData["Mercaster"] = 0
                    ironData["Leabia"] = 0
                    ironData["Cracaphill"] = 0
                    transaction.set(documentReferenceInventory!!, ironData)
                    null
                }
                .addOnSuccessListener {
                    startActivity(
                            Intent(this,
                                    MainOptionsActivity::class.java))
                }
    }

    private fun nicknameIsValid(): Boolean {
        return register_username.text.length > 2
    }
}
