package com.artamonov.millionplanets

import android.content.Intent
import android.view.View

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.User
import com.google.android.material.snackbar.Snackbar
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

    fun onGoToSpace(view: View) {
        if (!nicknameIsValid()) {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Your nickname must be at least 3 characters",
                    Snackbar.LENGTH_SHORT)
                    .show()
            return
        }
        firebaseAuth = FirebaseAuth.getInstance()
        username = register_username.text.toString()
        firebaseUser = firebaseAuth.currentUser
        documentReferenceInventory = firebaseFirestore.collection("Inventory").document(username)
        documentReferenceObjects = firebaseFirestore.collection("Objects").document(username)
        user = User()
        user!!.x = 5
        user!!.y = 6
        user!!.cargoCapacity = 10
        user!!.hp = 50
        user!!.ship = "Fighter"
        user!!.money = 1000
        user!!.scanner_capacity = 15
        user!!.shield = 100
        user!!.jump = 10
        user!!.fuel = 20
        user!!.nickname = username
        user!!.email = firebaseUser!!.email
        user!!.type = "user"
        user!!.sumXY = 11
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
                            Intent(
                                    applicationContext,
                                    MainOptionsActivity::class.java))
                }

        /* private void setGeoData() {
            ObjectModel objectModel = new ObjectModel();
            objectModel.setX(user.getX());
            objectModel.setY(user.getY());
            objectModel.setSumXY(user.getSumXY());
            objectModel.setType("user");

            firebaseFirestore.collection("GeoData").document(username)
                    .set(objectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(getApplicationContext(), MainOptionsActivity.class));
                }
            });
        }*/
    }

    private fun nicknameIsValid(): Boolean {
        return register_username.text.length > 2
    }
}
