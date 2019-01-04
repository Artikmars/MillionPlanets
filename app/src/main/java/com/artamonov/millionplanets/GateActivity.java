package com.artamonov.millionplanets;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.artamonov.millionplanets.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

public class GateActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";
    User userList = new User();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHp;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tvFuel;
    private TextView tvMoney;
    private TextView tv_ScannerCapacity;
    private Button btnGateAction;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private DocumentReference documentReferenceResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gate);

        tvPosition = findViewById(R.id.gate_coordinates);
        tvShip = findViewById(R.id.gate_ship);
        tvHp = findViewById(R.id.gate_hp);
        tvShield = findViewById(R.id.gate_shield);
        tvCargo = findViewById(R.id.gate_cargo);
        tvFuel = findViewById(R.id.gate_fuel);
        tv_ScannerCapacity = findViewById(R.id.gate_scanner_capacity);
        tvMoney = findViewById(R.id.gate_money);
        btnGateAction = findViewById(R.id.gate_action);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser.getDisplayName());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot doc, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (doc.exists()) {
                    userList.setShip(doc.getString("ship"));
                    userList.setX((doc.getLong("x").intValue()));
                    userList.setY((doc.getLong("y").intValue()));
                    userList.setSumXY((doc.getLong("sumXY").intValue()));
                    userList.setHp(doc.getLong("hp").intValue());
                    userList.setCargo(doc.getLong("cargo").intValue());
                    userList.setFuel(doc.getLong("fuel").intValue());
                    userList.setScanner_capacity(doc.getLong("scanner_capacity").intValue());
                    userList.setShield(doc.getLong("shield").intValue());
                    userList.setMoney(doc.getLong("money").intValue());
                    userList.setMoveToObjectName(doc.getString("moveToObjectName"));
                    userList.setMoveToObjectType(doc.getString("moveToObjectType"));


                    tvPosition.setText(String.format(getResources().getString(R.string.current_coordinate),
                            userList.getX(), userList.getY()));
                    tvShip.setText(userList.getShip());
                    tvHp.setText(Integer.toString(userList.getHp()));
                    tvShield.setText(Integer.toString(userList.getShield()));
                    tvCargo.setText(Integer.toString(userList.getCargo()));
                    tv_ScannerCapacity.setText(Integer.toString(userList.getScanner_capacity()));
                    tvFuel.setText(Integer.toString(userList.getFuel()));
                    tvMoney.setText(Integer.toString(userList.getMoney()));


                    switch (userList.getMoveToObjectType()) {
                        case "planet":
                            btnGateAction.setText(getResources().getString(R.string.land));
                            break;
                        case "user":
                            btnGateAction.setText(getResources().getString(R.string.fight));
                            break;
                        case "fuel":
                            btnGateAction.setText(getResources().getString(R.string.get_fuel));
                            break;
                        // case "meteoroid_field":
                        case "debris":
                            btnGateAction.setText(getResources().getString(R.string.mine));
                            break;
                        case "meteorite_field":
                            btnGateAction.setText(getResources().getString(R.string.mine));
                            break;
                    }
                }
            }

        });

        documentReferenceResources = firebaseFirestore.collection("Inventory")
                .document(firebaseUser.getDisplayName());
        documentReferenceResources.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userList.setResource_iron(documentSnapshot.getLong("Iron").intValue());
            }
        });


    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }

    private void mineIron() {
        btnGateAction.setBackgroundColor(getResources().getColor(R.color.grey));
        btnGateAction.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btnGateAction.setEnabled(true);
                        btnGateAction.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    }
                });
            }
        }, 5000);

       /* float i = new Random().nextFloat();
        if (i > 0 && i < 0.3) {
            Toast.makeText(this, "You got 1 iron. ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Fail :( Try again. ", Toast.LENGTH_SHORT).show();
        }*/

        final double random = new Random().nextDouble() * 100;
        if (random >= 0 && random <= 30) {
            Toast.makeText(this, "Fail :( Try again. Total: " + userList.getResource_iron(), Toast.LENGTH_LONG).show();
        }
        if (random > 30 && random <= 40) {
            if (userList.getResource_iron() + 1 <= userList.getCargo()) {
                Toast.makeText(this, "You got 1 iron. Total: " + (userList.getResource_iron() + 1), Toast.LENGTH_LONG).show();
                documentReferenceResources.update("Iron", userList.getResource_iron() + 1);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 40 && random <= 60) {
            if (userList.getResource_iron() + 2 <= userList.getCargo()) {
                Toast.makeText(this, "You got 2 iron. Total: " + (userList.getResource_iron() + 2), Toast.LENGTH_LONG).show();
                documentReferenceResources.update("Iron", userList.getResource_iron() + 2);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 60 && random <= 80) {

            if (userList.getResource_iron() + 3 <= userList.getCargo()) {
                Toast.makeText(this, "You got 3 iron. Total: " + (userList.getResource_iron() + 3), Toast.LENGTH_LONG).show();
                documentReferenceResources.update("Iron", userList.getResource_iron() + 3);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 80 && random <= 90) {
            if (userList.getResource_iron() + 5 <= userList.getCargo()) {
                Toast.makeText(this, "You got 5 iron. Total: " + (userList.getResource_iron() + 5), Toast.LENGTH_LONG).show();
                documentReferenceResources.update("Iron", userList.getResource_iron() + 5);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 90 && random <= 95) {
            if (userList.getResource_iron() + 10 <= userList.getCargo()) {
                Toast.makeText(this, "You got 10 iron. Total: " + (userList.getResource_iron() + 10), Toast.LENGTH_LONG).show();
                documentReferenceResources.update("Iron", userList.getResource_iron() + 10);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 95 && random <= 97.5) {
            if (userList.getResource_iron() + 50 <= userList.getCargo()) {
                Toast.makeText(this, "You got 50 iron. Total: " + (userList.getResource_iron() + 50), Toast.LENGTH_LONG).show();
                documentReferenceResources.update("Iron", userList.getResource_iron() + 50);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 97.5 && random <= 100) {
            if (userList.getResource_iron() + 100 <= userList.getCargo()) {
                Toast.makeText(this, "You got 100 iron. Total: " + (userList.getResource_iron() + 100), Toast.LENGTH_LONG).show();
                documentReferenceResources.update("Iron", userList.getResource_iron() + 100);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }
        }


    }


    public void onJump(View view) {
        switch (userList.getMoveToObjectType()) {
            case "planet":

                Intent intent = new Intent(this, PlanetActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } else {
                    startActivity(intent);
                }

                // startActivity(new Intent(this, PlanetActivity.class));
                break;
            case "user":
                btnGateAction.setText(getResources().getString(R.string.fight));
                break;
            case "fuel":
                documentReference.update("fuel", 20);
                break;
            //case "meteoroid_field":
            case "debris":
                mineIron();
                break;
            case "meteorite_field":
                mineIron();
                break;
        }
    }

    public void onScan(View view) {

        Intent intent = new Intent(this, ScanResultActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }
}
