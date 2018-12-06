package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_player_activity);
        etNickname = findViewById(R.id.player_name);
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    public void onGoToSpace(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        username = etNickname.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username).build();
        firebaseUser.updateProfile(profileUpdates);

        User user = new User();
        user.setPosition("43");
        user.setCargo("0");
        user.setHull("100");
        user.setShip("Fighter");
        user.setScanner_capacity("5");
        user.setShield("100");
        firebaseFirestore.collection("UserData").document(firebaseUser.getEmail())
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Added new data", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainOptionsActivity.class));
            }
        });




    }
}
