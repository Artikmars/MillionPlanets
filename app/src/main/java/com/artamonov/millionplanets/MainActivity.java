package com.artamonov.millionplanets;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Button btnContinue;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnContinue = findViewById(R.id.continue_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            btnContinue.setVisibility(View.VISIBLE);
        }

    }

    public void onNewGame(View view) {
        Intent intent = new Intent(this, FirebaseAuthentication.class);
        startActivity(intent);
    }

    public void onContinue(View view) {
        startActivity(new Intent(getApplicationContext(), MainOptionsActivity.class));
    }

}
