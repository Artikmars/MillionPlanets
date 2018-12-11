package com.artamonov.millionplanets;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainOptionsActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    User userList = new User();
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHull;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tv_ScannerCapacity;
    ProgressDialog progressDialog;
    private TextView tvFuel;
    private View parentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_options);

        tvPosition = findViewById(R.id.coordinates);
        tvShip = findViewById(R.id.ship);
        tvHull = findViewById(R.id.hull);
        tvShield = findViewById(R.id.shield);
        tvCargo = findViewById(R.id.cargo);
        tv_ScannerCapacity = findViewById(R.id.scanner_capacity);
        tvFuel = findViewById(R.id.fuel);

        progressDialog = new ProgressDialog(this);
        // progressDialog.setMessage("Loading Data . . .");
        // progressDialog.setTitle("In Progress");
        progressDialog.setCancelable(false);
        // progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Welcome on board, " + firebaseUser.getDisplayName() + "!",
                Snackbar.LENGTH_LONG).show();

        DocumentReference user = firebaseFirestore.collection("UserData").document(firebaseUser.getEmail());
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    userList.setShip(doc.getString("ship"));
                    //    userList.setPosition(doc.getString("position"));
                    userList.setX((doc.getLong("x").intValue()));
                    userList.setY((doc.getLong("y").intValue()));
                    userList.setSumXY((doc.getLong("sumXY").intValue()));
                    userList.setHull(doc.getString("hull"));
                    userList.setCargo(doc.getString("cargo"));
                    userList.setFuel(doc.getString("fuel"));
                    userList.setScanner_capacity(doc.getLong("scanner_capacity").intValue());
                    userList.setShield(doc.getString("shield"));

                    String currentPosition = String.format(getResources().getString(R.string.current_coordinate),
                            userList.getX(), userList.getY());
                    userList.setPosition(currentPosition);

                    tvPosition.setText(userList.getPosition());
                    tvShip.setText(userList.getShip());
                    tvHull.setText(userList.getHull());
                    tvShield.setText(userList.getShield());
                    tvCargo.setText(userList.getCargo());
                    tv_ScannerCapacity.setText(Integer.toString(userList.getScanner_capacity()));
                    tvFuel.setText(userList.getFuel());

                    progressDialog.dismiss();
                }
            }
        });

    }

    public void onScan(View view) {
        Intent intent = new Intent(this, ScanResultActivity.class);
        intent.putExtra("x", userList.getX());
        intent.putExtra("y", userList.getY());
        intent.putExtra("sumXY", userList.getSumXY());
        intent.putExtra("hull", userList.getHull());
        intent.putExtra("cargo", userList.getCargo());
        intent.putExtra("scanner_capacity", userList.getScanner_capacity());
        intent.putExtra("shield", userList.getShield());
        intent.putExtra("ship", userList.getShip());
        intent.putExtra("position", userList.getPosition());
        intent.putExtra("fuel", userList.getFuel());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }

}
