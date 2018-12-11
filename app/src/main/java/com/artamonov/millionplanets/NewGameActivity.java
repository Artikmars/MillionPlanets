package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewGameActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText etNickname;
    FirebaseFirestore firebaseFirestore;
    String username;
    private FirebaseUser firebaseUser;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_player_activity);
        etNickname = findViewById(R.id.player_name);
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    public void onGoToSpace(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        username = etNickname.getText().toString();
        firebaseUser = firebaseAuth.getCurrentUser();

        user = new User();
        //  user.setPosition("4;6");
        user.setX(4);
        user.setY(6);
        user.setSumXY(user.getX() + user.getY());
        user.setCargo("0");
        user.setHull("100");
        user.setShip("Fighter");
        user.setScanner_capacity(5);
        user.setShield("100");
        user.setFuel("20");
        user.setNickname(username);
        // Log.i("myTags", "nickname: " + firebaseUser.getDisplayName());
        firebaseFirestore.collection("UserData").document(firebaseUser.getEmail())
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();
                firebaseUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setGeoData();
                    }
                });

            }
        });
    }

    private void setGeoData() {
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
    }

}
