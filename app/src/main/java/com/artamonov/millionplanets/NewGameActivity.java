package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import java.util.HashMap;
import java.util.Map;

public class NewGameActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText etNickname;
    FirebaseFirestore firebaseFirestore;
    String username;
    private DocumentReference documentReferenceInventory;
    private DocumentReference documentReferenceObjects;
    private FirebaseUser firebaseUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_player_activity);
        etNickname = findViewById(R.id.register_username);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void onGoToSpace(View view) {
        if (!nicknameIsValid()) {
            Snackbar.make(
                            findViewById(android.R.id.content),
                            "Your nickname must be at least 3 characters",
                            Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        firebaseAuth = FirebaseAuth.getInstance();
        username = etNickname.getText().toString();
        firebaseUser = firebaseAuth.getCurrentUser();
        documentReferenceInventory = firebaseFirestore.collection("Inventory").document(username);
        documentReferenceObjects = firebaseFirestore.collection("Objects").document(username);
        user = new User();
        user.setX(5);
        user.setY(6);
        user.setCargo(10);
        user.setHp(50);
        user.setShip("Fighter");
        user.setMoney(1000);
        user.setScanner_capacity(15);
        user.setShield(100);
        user.setJump(10);
        user.setFuel(20);
        user.setNickname(username);
        user.setEmail(firebaseUser.getEmail());
        user.setType("user");
        user.setSumXY(11);
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
                .runTransaction(
                        new Transaction.Function<Void>() {

                            @Override
                            public Void apply(Transaction transaction) {
                                transaction.set(documentReferenceObjects, user);
                                UserProfileChangeRequest profileUpdates =
                                        new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();
                                firebaseUser.updateProfile(profileUpdates);
                                Map<String, Object> ironData = new HashMap<>();
                                ironData.put("Iron", 0);
                                ironData.put("Mercaster", 0);
                                ironData.put("Leabia", 0);
                                ironData.put("Cracaphill", 0);
                                transaction.set(documentReferenceInventory, ironData);
                                return null;
                            }
                        })
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(
                                        new Intent(
                                                getApplicationContext(),
                                                MainOptionsActivity.class));
                            }
                        });

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

    private boolean nicknameIsValid() {
        return etNickname.getText().length() > 2;
    }
}
