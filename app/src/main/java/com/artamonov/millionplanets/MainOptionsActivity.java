package com.artamonov.millionplanets;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.artamonov.millionplanets.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainOptionsActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    User userList = new User();
    ProgressDialog progressDialog;
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHp;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tv_ScannerCapacity;
    private TextView tvFuel;
    private TextView tvMoney;
    private View parentLayout;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_options);

        tvPosition = findViewById(R.id.coordinates);
        tvShip = findViewById(R.id.ship);
        tvHp = findViewById(R.id.hp);
        tvShield = findViewById(R.id.shield);
        tvCargo = findViewById(R.id.cargo);
        tv_ScannerCapacity = findViewById(R.id.scanner_capacity);
        tvFuel = findViewById(R.id.fuel);
        tvMoney = findViewById(R.id.money);

        /*  progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();*/

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        parentLayout = findViewById(android.R.id.content);
        Snackbar.make(
                        parentLayout,
                        "Welcome on board, " + firebaseUser.getDisplayName() + "!",
                        Snackbar.LENGTH_LONG)
                .show();

        /*   documentReference = firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    userList.setShip(doc.getString("ship"));
                    //    userList.setPosition(doc.getString("position"));
                    userList.setX((doc.getLong("x").intValue()));
                    userList.setY((doc.getLong("y").intValue()));
                    userList.setHp(doc.getLong("hp").intValue());
                    userList.setCargo(doc.getLong("cargo").intValue());
                    userList.setFuel(doc.getLong("fuel").intValue());
                    userList.setScanner_capacity(doc.getLong("scanner_capacity").intValue());
                    userList.setShield(doc.getLong("shield").intValue());
                    userList.setMoney(doc.getLong("money").intValue());


                    tvPosition.setText(String.format(getResources().getString(R.string.current_coordinate),
                            userList.getX(), userList.getY()));
                    tvShip.setText(userList.getShip());
                    tvHp.setText(Integer.toString(userList.getHp()));
                    tvShield.setText(Integer.toString(userList.getShield()));
                    tvCargo.setText(Integer.toString(userList.getCargo()));
                    tv_ScannerCapacity.setText(Integer.toString(userList.getScanner_capacity()));
                    tvFuel.setText(Integer.toString(userList.getFuel()));
                    progressDialog.dismiss();
                }
            }
        });*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        // progressDialog.setMessage("Loading Data . . .");
        // progressDialog.setTitle("In Progress");
        progressDialog.setCancelable(false);
        // progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReference =
                firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        documentReference.addSnapshotListener(
                this,
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(
                            @javax.annotation.Nullable DocumentSnapshot doc,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (doc.exists()) {
                            userList.setShip(doc.getString("ship"));
                            userList.setX((doc.getLong("x").intValue()));
                            userList.setY((doc.getLong("y").intValue()));
                            userList.setSumXY((doc.getLong("sumXY").intValue()));
                            userList.setHp(doc.getLong("hp").intValue());
                            userList.setCargo(doc.getLong("cargo").intValue());
                            userList.setFuel(doc.getLong("fuel").intValue());
                            userList.setScanner_capacity(
                                    doc.getLong("scanner_capacity").intValue());
                            userList.setShield(doc.getLong("shield").intValue());
                            userList.setMoney(doc.getLong("money").intValue());
                            userList.setMoveToObjectName(doc.getString("moveToObjectName"));
                            userList.setMoveToObjectType(doc.getString("moveToObjectType"));

                            tvPosition.setText(
                                    String.format(
                                            getResources().getString(R.string.current_coordinate),
                                            userList.getX(),
                                            userList.getY()));
                            tvShip.setText(userList.getShip());
                            tvHp.setText(Integer.toString(userList.getHp()));
                            tvShield.setText(Integer.toString(userList.getShield()));
                            tvCargo.setText(Integer.toString(userList.getCargo()));
                            tv_ScannerCapacity.setText(
                                    Integer.toString(userList.getScanner_capacity()));
                            tvFuel.setText(Integer.toString(userList.getFuel()));
                            tvMoney.setText(Integer.toString(userList.getMoney()));

                            progressDialog.dismiss();
                        }
                    }
                });
    }

    public void onScan(View view) {
        Intent intent = new Intent(this, ScanResultActivity.class);
        /*  intent.putExtra("x", userList.getX());
        intent.putExtra("y", userList.getY());
        intent.putExtra("hull", userList.getHp());
        intent.putExtra("cargo", userList.getCargo());
        intent.putExtra("scanner_capacity", userList.getScanner_capacity());
        intent.putExtra("shield", userList.getShield());
        intent.putExtra("ship", userList.getShip());
        intent.putExtra("fuel", userList.getFuel());*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }
}
