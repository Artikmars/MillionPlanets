package com.artamonov.millionplanets;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import utils.RandomUtils;

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
    private DocumentReference documentReferenceInventory;
    private DocumentReference documentReferencePlanet;
    private ObjectModel objectModel = new ObjectModel();
    private int count;
    private long maxTimeInMilliseconds;
    private boolean debrisIsOver;
    private CountDownTimer countDownTimer;
    private long remainedSecs;


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
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @androidx.annotation.Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                userList.setMoveToObjectName(documentSnapshot.getString("moveToObjectName"));
                documentReferencePlanet = firebaseFirestore.collection("Objects")
                        .document(userList.getMoveToObjectName());
                DocumentSnapshot documentSnapshot2 = transaction.get(documentReferencePlanet);
                objectModel.setDebrisIronAmount(documentSnapshot2.getLong("iron").intValue());
                transaction.update(documentReference, "moveToObjectName", userList.getMoveToObjectName());
                transaction.update(documentReferencePlanet, "iron", objectModel.getDebrisIronAmount());
                return null;
            }
        });
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot doc, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (doc.exists()) {
                    userList.setMoveToObjectName(doc.getString("moveToObjectName"));
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

        documentReferenceInventory = firebaseFirestore.collection("Inventory")
                .document(firebaseUser.getDisplayName());
        documentReferenceInventory.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userList.setResource_iron(documentSnapshot.getLong("Iron").intValue());
            }
        });

       /* documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userList.setFuel(documentSnapshot.getLong("fuel").intValue());
                userList.setMoney(documentSnapshot.getLong("money").intValue());
            }
        });*/


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
                documentReferenceInventory.update("Iron", userList.getResource_iron() + 1);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 40 && random <= 60) {
            if (userList.getResource_iron() + 2 <= userList.getCargo()) {
                Toast.makeText(this, "You got 2 iron. Total: " + (userList.getResource_iron() + 2), Toast.LENGTH_LONG).show();
                documentReferenceInventory.update("Iron", userList.getResource_iron() + 2);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 60 && random <= 80) {

            if (userList.getResource_iron() + 3 <= userList.getCargo()) {
                Toast.makeText(this, "You got 3 iron. Total: " + (userList.getResource_iron() + 3), Toast.LENGTH_LONG).show();
                documentReferenceInventory.update("Iron", userList.getResource_iron() + 3);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 80 && random <= 90) {
            if (userList.getResource_iron() + 5 <= userList.getCargo()) {
                Toast.makeText(this, "You got 5 iron. Total: " + (userList.getResource_iron() + 5), Toast.LENGTH_LONG).show();
                documentReferenceInventory.update("Iron", userList.getResource_iron() + 5);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 90 && random <= 95) {
            if (userList.getResource_iron() + 10 <= userList.getCargo()) {
                Toast.makeText(this, "You got 10 iron. Total: " + (userList.getResource_iron() + 10), Toast.LENGTH_LONG).show();
                documentReferenceInventory.update("Iron", userList.getResource_iron() + 10);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 95 && random <= 97.5) {
            if (userList.getResource_iron() + 50 <= userList.getCargo()) {
                Toast.makeText(this, "You got 50 iron. Total: " + (userList.getResource_iron() + 50), Toast.LENGTH_LONG).show();
                documentReferenceInventory.update("Iron", userList.getResource_iron() + 50);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }

        }
        if (random > 97.5 && random <= 100) {
            if (userList.getResource_iron() + 100 <= userList.getCargo()) {
                Toast.makeText(this, "You got 100 iron. Total: " + (userList.getResource_iron() + 100), Toast.LENGTH_LONG).show();
                documentReferenceInventory.update("Iron", userList.getResource_iron() + 100);
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show();
            }
        }


    }

    public void onJump(View view) {

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            //maxTimeInMilliseconds/1000 indicates how much resources are going to be mined until the cargo is fulled
            documentReferenceInventory.update("Iron", userList.getResource_iron() + maxTimeInMilliseconds / 1000 - remainedSecs);
            documentReferencePlanet.update("iron", objectModel.getDebrisIronAmount() - (maxTimeInMilliseconds / 1000 - remainedSecs));
            btnGateAction.setText(getResources().getString(R.string.mine));
            return;
        }

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
                Integer fuelToFill = 20 - userList.getFuel();
                Integer price = fuelToFill * 1000;
                if (userList.getMoney() >= price) {
                    documentReference.update("fuel", 20);
                    documentReference.update("money", userList.getMoney() - price);
                }
                break;
            //case "meteoroid_field":
            case "debris":
                mineDebris();
                break;
            case "meteorite_field":
                mineIron();
                break;
        }
    }

    private void createDebris() {
        Map<String, Object> debris = new HashMap<>();
        int x = RandomUtils.getRandomCoordinate();
        int y = RandomUtils.getRandomCoordinate();
        debris.put("x", x);
        debris.put("y", y);
        debris.put("sumXY", x + y);
        debris.put("type", "debris");
        debris.put("iron", RandomUtils.getRandomDebrisIron());

        CollectionReference collectionReferencePlanet = firebaseFirestore.collection("Objects");
        collectionReferencePlanet.document("Debris-" + RandomUtils.getRandomDebrisName(4))
                .set(debris);
    }


    public void startTimer(final long finish, long tick, final boolean debrisIsOver) {

        countDownTimer = new CountDownTimer(finish, tick) {

            public void onTick(long millisUntilFinished) {
                remainedSecs = millisUntilFinished / 1000;
                btnGateAction.setText(getResources().getString(R.string.mine_stop) + " " + (remainedSecs / 60) + ":" + (remainedSecs % 60));

            }

            public void onFinish() {

                //   btnGateAction.setText(getResources().getString(R.string.mine));
                //  btnGateAction.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                //  btnGateAction.setEnabled(true);
                documentReferenceInventory.update("Iron", userList.getResource_iron() + maxTimeInMilliseconds / 1000);
                if (debrisIsOver) {
                    documentReferencePlanet.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(GateActivity.this, "Debris is empty and is not available anymore", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GateActivity.this, MainOptionsActivity.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(GateActivity.this).toBundle());
                            } else {
                                startActivity(intent);
                            }
                            // startActivity(new Intent(GateActivity.this, MainOptionsActivity.class));
                        }
                    });
                } else {
                    documentReferencePlanet.update("iron", objectModel.getDebrisIronAmount() - maxTimeInMilliseconds / 1000);
                    Toast.makeText(GateActivity.this, "Mining is over. Cargo is full", Toast.LENGTH_SHORT).show();
                }
                btnGateAction.setText(getResources().getString(R.string.mine));
                cancel();
                countDownTimer = null;

            }
        }.start();
    }

    private void mineDebris() {

        debrisIsOver = false;
        //btnGateAction.setBackgroundColor(getResources().getColor(R.color.grey));
        //btnGateAction.setEnabled(false);
        maxTimeInMilliseconds = 1000 * (userList.getCargo() - userList.getResource_iron());
        // If the iron amount on debris less than an amount that is needed to fill up fully the cargo
        if (objectModel.getDebrisIronAmount() < maxTimeInMilliseconds / 1000) {
            maxTimeInMilliseconds = 1000 * objectModel.getDebrisIronAmount();
            debrisIsOver = true;
            createDebris();
        }
        startTimer(maxTimeInMilliseconds, 1000, debrisIsOver);

        /*int delay = 2000; // delay for 5 sec.
        int period = 1000; // repeat every sec.
        count = 0;
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.i("myTags", "run: debris Iron: " + objectModel.getDebrisIronAmount());
                Log.i("myTags", "run: cargo: " + userList.getCargo());
                Log.i("myTags", "run: Iron: " + userList.getResource_iron());
                if (objectModel.getDebrisIronAmount() <=0) {
                    timer.cancel();
                }
                if (userList.getResource_iron() + 5 < userList.getCargo()) {
                    count++;
                    documentReferenceInventory.update("Iron", userList.getResource_iron() + 5);
                    documentReferencePlanet.update("iron", objectModel.getIronAmount() - 5);
                } else {
                    timer.cancel();
                }
            }
        }, delay, period);*/

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
