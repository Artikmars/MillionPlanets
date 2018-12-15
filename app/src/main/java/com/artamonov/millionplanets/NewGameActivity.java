package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

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
        user.setX(5);
        user.setY(6);
        user.setCargo(5);
        user.setHp(100);
        user.setShip("Fighter");
        user.setMoney(1000);
        user.setScanner_capacity(10);
        user.setShield(100);
        user.setFuel(20);
        user.setNickname(username);
        user.setEmail(firebaseUser.getEmail());
        user.setType("user");
        user.setSumXY(11);
        // Log.i("myTags", "nickname: " + firebaseUser.getDisplayName());
        firebaseFirestore.collection("Objects").document(username)
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
