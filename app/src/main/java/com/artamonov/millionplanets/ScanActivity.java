package com.artamonov.millionplanets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    User userList = new User();
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHull;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tv_ScannerCapacity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);

        tvPosition = findViewById(R.id.coordinates);
        tvShip = findViewById(R.id.ship);
        tvHull = findViewById(R.id.hull);
        tvShield = findViewById(R.id.shield);
        tvCargo = findViewById(R.id.cargo);
        tv_ScannerCapacity = findViewById(R.id.scanner_capacity);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference user = firebaseFirestore.collection("UserData").document(firebaseUser.getEmail());
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    userList.setShip(doc.getString("ship"));
                    userList.setPosition(doc.getString("position"));
                    userList.setHull(doc.getString("hull"));
                    userList.setCargo(doc.getString("cargo"));
                    userList.setScanner_capacity(doc.getString("scanner_capacity"));
                    userList.setShield(doc.getString("shield"));

                    tvPosition.setText(userList.getPosition());
                    tvShip.setText(userList.getShip());
                    tvHull.setText(userList.getHull());
                    tvShield.setText(userList.getShield());
                    tvCargo.setText(userList.getCargo());
                    tv_ScannerCapacity.setText(userList.getScanner_capacity());

                }
            }
        });

    }


}
