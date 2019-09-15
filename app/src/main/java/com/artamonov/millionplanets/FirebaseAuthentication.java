package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class FirebaseAuthentication extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText etEmail;
    EditText etPassword;
    Button bLogin;
    String email;
    String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firebase_auth);
        etEmail = findViewById(R.id.firebase_email);
        etPassword = findViewById(R.id.firebase_password);
        bLogin = findViewById(R.id.register_login_firebase);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void onSubmit(View view) {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.getException()
                                        instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(
                                                    getApplicationContext(),
                                                    "The email is already occupied",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }

                                if (task.isSuccessful()) {
                                    startActivity(
                                            new Intent(
                                                    getApplicationContext(),
                                                    NewGameActivity.class));
                                }
                            }
                        });
    }
}
