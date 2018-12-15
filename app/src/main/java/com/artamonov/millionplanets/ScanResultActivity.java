package com.artamonov.millionplanets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.artamonov.millionplanets.adapter.ScanResultAdapter;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanResultActivity extends AppCompatActivity implements ScanResultAdapter.ItemClickListener {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    List<ObjectModel> objectModelList;
    RecyclerView rvScanResult;
    User userList = new User();
    ProgressDialog progressDialog;
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHp;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tv_ScannerCapacity;
    private TextView tvFuel;
    private DocumentReference documentReference;
    private View parentLayout;

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
                    tvPosition.setText(String.format(getResources().getString(R.string.current_coordinate),
                            userList.getX(), userList.getY()));
                    tvShip.setText(userList.getShip());
                    tvHp.setText(Integer.toString(userList.getHp()));
                    tvShield.setText(Integer.toString(userList.getShield()));
                    tvCargo.setText(Integer.toString(userList.getCargo()));
                    tv_ScannerCapacity.setText(Integer.toString(userList.getScanner_capacity()));
                    tvFuel.setText(Integer.toString(userList.getFuel()));


                    CollectionReference objectRef = firebaseFirestore.collection("Objects");
                    Log.i("myLogs", "sumXY: + " + (userList.getSumXY() + userList.getScanner_capacity()));
                    Log.i("myLogs", "sumXY: - " + (userList.getSumXY() - userList.getScanner_capacity()));
                    objectRef.whereLessThanOrEqualTo("sumXY", userList.getSumXY() + userList.getScanner_capacity())
                            .whereGreaterThanOrEqualTo("sumXY", userList.getSumXY() - userList.getScanner_capacity())
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            objectModelList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.i("myLogs", "document.getId: " + document.getId());
                                ObjectModel objectModel = new ObjectModel();
                                objectModel.setName(document.getId());
                                objectModel.setType(document.getString("type"));
                                objectModel.setX(document.getLong("x").intValue());
                                objectModel.setY(document.getLong("y").intValue());

                                //  Distinguish between (2;8) and (3;7)
                                if (document.getLong("sumXY").intValue() == userList.getSumXY()) {
                                    objectModel.setDistance(Math.abs(document.getLong("x").intValue() - userList.getX()));
                                } else {
                                    objectModel.setDistance(Math.abs(document.getLong("sumXY").intValue() - userList.getSumXY()));
                                }

                                if (objectModel.getDistance() <= userList.getScanner_capacity()) {
                                    objectModelList.add(objectModel);
                                }
                            }

                            //Excluding the user itself in the search
                            for (int i = 0; i < objectModelList.size(); i++) {
                                if (objectModelList.get(i).getName().equals(firebaseUser.getDisplayName())) {
                                    objectModelList.remove(i);
                                }
                            }

                            Collections.sort(objectModelList, new Comparator<ObjectModel>() {
                                @Override
                                public int compare(ObjectModel objectModel, ObjectModel t1) {
                                    return (objectModel.getDistance() - t1.getDistance());
                                }
                            });

                            ScanResultAdapter scanResultAdapter = new ScanResultAdapter(objectModelList, ScanResultActivity.this);
                            rvScanResult.setAdapter(scanResultAdapter);
                            scanResultAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }

        });

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_result);
        rvScanResult = findViewById(R.id.scan_result_list);
        rvScanResult.setLayoutManager(new LinearLayoutManager(this));




      /*  progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();*/

        tvPosition = findViewById(R.id.scan_coordinates);
        tvShip = findViewById(R.id.scan_ship);
        tvHp = findViewById(R.id.scan_hp);
        tvShield = findViewById(R.id.scan_shield);
        tvCargo = findViewById(R.id.scan_cargo);
        tv_ScannerCapacity = findViewById(R.id.scan_scanner_capacity);
        tvFuel = findViewById(R.id.scan_fuel);
    }

   /* View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
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
    };*/

    public void onGoBackToMainOptions(View view) {
        finish();
    }


   /* private int getFuelRate(int x1, int x2, int y1, int y2, long current_fuel) {

    }*/

    @Override
    public void onItemClick(int pos) {


        Map<String, Object> moveToObject = new HashMap<>();
        moveToObject.put("moveToObjectName", objectModelList.get(pos).getName());
        moveToObject.put("moveToObjectType", objectModelList.get(pos).getType());
        moveToObject.put("moveToObjectDistance", objectModelList.get(pos).getDistance());
        //  moveToObject.put("fuel", userList.getFuel() - objectModelList.get(pos).getDistance());
        documentReference.update(moveToObject);

        Intent intent = new Intent(this, MoveActivity.class);

        // Show only the recycler view item which was clicked
        ObjectModel objectModel = objectModelList.get(pos);
        intent.putExtra("objectType", objectModel.getType());
        intent.putExtra("objectName", objectModel.getName());
        intent.putExtra("objectDistance", objectModel.getDistance());
        intent.putExtra("objectX", objectModel.getX());
        intent.putExtra("objectY", objectModel.getY());
        Log.i("myLogs", "onItemClick in ScanResult: x: " + objectModel.getX());
        Log.i("myLogs", "onItemClick in ScanResult: y: " + objectModel.getY());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(ScanResultActivity.this,
                            rvScanResult,
                            ViewCompat.getTransitionName(rvScanResult));

            //  startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }
}