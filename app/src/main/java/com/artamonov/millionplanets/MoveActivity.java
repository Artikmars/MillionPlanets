package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.artamonov.millionplanets.adapter.ScanResultAdapter;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MoveActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";
    RecyclerView rvScanResult;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    User userList = new User();
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHp;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tvFuel;
    private TextView tv_ScannerCapacity;
    private FirebaseUser firebaseUser;
    private View parentLayout;
    private ObjectModel objectModel;
    private DocumentReference documentReference;
    //   private ProgressDialog progressDialog;
    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getApplicationContext(), "Please, wait 10 seconds", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    documentReference.update("fuel", 20);
                }
            }, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        parentLayout = findViewById(android.R.id.content);
        tvPosition = findViewById(R.id.move_coordinates);
        tvShip = findViewById(R.id.move_ship);
        tvHp = findViewById(R.id.move_hp);
        tvShield = findViewById(R.id.move_shield);
        tvCargo = findViewById(R.id.move_cargo);
        tvFuel = findViewById(R.id.move_fuel);
        tv_ScannerCapacity = findViewById(R.id.move_scanner_capacity);
        rvScanResult = findViewById(R.id.move_scan_result_list);
        rvScanResult.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent();
        List<ObjectModel> objectModelList = new ArrayList<>();
        objectModel = new ObjectModel();
        objectModel.setType(intent.getStringExtra("objectType"));
        objectModel.setName(intent.getStringExtra("objectName"));
        objectModel.setDistance(intent.getIntExtra("objectDistance", 0));
        objectModel.setX(intent.getIntExtra("objectX", 0));
        objectModel.setY(intent.getIntExtra("objectY", 0));
        objectModelList.add(objectModel);

        ScanResultAdapter scanResultAdapter = new ScanResultAdapter(objectModelList);
        rvScanResult.setAdapter(scanResultAdapter);
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
                    userList.setMoveToObjectDistance(doc.getLong("moveToObjectDistance").intValue());


                    tvPosition.setText(String.format(getResources().getString(R.string.current_coordinate),
                            userList.getX(), userList.getY()));
                    tvShip.setText(userList.getShip());
                    tvHp.setText(Integer.toString(userList.getHp()));
                    tvShield.setText(Integer.toString(userList.getShield()));
                    tvCargo.setText(Integer.toString(userList.getCargo()));
                    tv_ScannerCapacity.setText(Integer.toString(userList.getScanner_capacity()));
                    tvFuel.setText(Integer.toString(userList.getFuel()));
                }
            }

        });

    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }

    public void onJump(View view) {

        parentLayout = findViewById(android.R.id.content);
        Log.i("myLogs", "onItemClick: userList.getFuel()" + userList.getFuel() + ", objectModelList.get(pos).getDistance(): "
                + userList.getMoveToObjectDistance());
        if (userList.getFuel() - userList.getMoveToObjectDistance() < 0) {
            Snackbar.make(parentLayout, "You are run out of fuel! Please, call the tanker. ",
                    Snackbar.LENGTH_LONG).setAction(R.string.call_tanker, snackbarOnClickListener).setDuration(4000).show();
            return;
        }


        final DocumentReference docRefForMovedObject = firebaseFirestore.collection("Objects")
                .document(userList.getMoveToObjectName());
        docRefForMovedObject.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int x = documentSnapshot.getLong("x").intValue();
                int y = documentSnapshot.getLong("y").intValue();
                Map<String, Object> movedPosition = new HashMap<>();
                movedPosition.put("x", x);
                movedPosition.put("y", y);
                movedPosition.put("fuel", userList.getFuel() - userList.getMoveToObjectDistance());
                movedPosition.put("sumXY", x + y);
                documentReference.update(movedPosition);
                startActivity(new Intent(getApplicationContext(), GateActivity.class));
            }
        });


        // Intent intent = new Intent(this, GateActivity.class);
        // intent.putExtra("objectType", objectModel.getType());
        // Log.i(TAG, "onJump x : " + objectModel.getX());
        // Log.i(TAG, "onJump: y : " + objectModel.getY());
        // intent.putExtra("objectX", objectModel.getX());
        //  intent.putExtra("objectY", objectModel.getY());
        //   startActivity(intent);

    }
}
