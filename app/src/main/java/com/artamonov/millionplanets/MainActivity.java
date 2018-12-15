package com.artamonov.millionplanets;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(getApplicationContext(), MainOptionsActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(new Intent(getApplicationContext(), MainOptionsActivity.class));
        }
    }

}
