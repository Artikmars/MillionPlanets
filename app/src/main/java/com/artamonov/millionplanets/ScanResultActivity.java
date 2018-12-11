package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScanResultActivity extends AppCompatActivity implements ScanResultAdapter.ItemClickListener {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    List<ObjectModel> objectModelList;
    RecyclerView rvScanResult;
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHull;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tv_ScannerCapacity;
    private TextView tvFuel;
    private Integer x;
    private Integer y;
    private Integer sumxy;
    private String cargo;
    private String hull;
    private String shield;
    private String ship;
    private String position;
    private String fuel;
    private Integer scanner_capacity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_result);
        rvScanResult = findViewById(R.id.scan_result_list);
        rvScanResult.setLayoutManager(new LinearLayoutManager(this));

        tvPosition = findViewById(R.id.scan_coordinates);
        tvShip = findViewById(R.id.scan_ship);
        tvHull = findViewById(R.id.scan_hull);
        tvShield = findViewById(R.id.scan_shield);
        tvCargo = findViewById(R.id.scan_cargo);
        tv_ScannerCapacity = findViewById(R.id.scan_scanner_capacity);
        tvFuel = findViewById(R.id.scan_scanner_capacity);


        Intent intent = getIntent();
        scanner_capacity = intent.getIntExtra("scanner_capacity", 0);
        x = intent.getIntExtra("x", 0);
        y = intent.getIntExtra("y", 0);
        sumxy = intent.getIntExtra("sumXY", 0);
        cargo = intent.getStringExtra("cargo");
        hull = intent.getStringExtra("hull");
        shield = intent.getStringExtra("shield");
        ship = intent.getStringExtra("ship");
        position = intent.getStringExtra("position");


        // tvPosition.setText(userList.getPosition());
        tvShip.setText(ship);
        tvHull.setText(hull);
        tvShield.setText(shield);
        tvCargo.setText(cargo);
        tv_ScannerCapacity.setText(String.valueOf(scanner_capacity));
        tvPosition.setText(position);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference objectRef = firebaseFirestore.collection("GeoData");
        //Query query = objectRef.whereLessThan("x", x + scanner_capacity).whereGreaterThan("x", x - scanner_capacity)
        //         .whereLessThan("y", y + scanner_capacity).whereGreaterThan("y", y - scanner_capacity);
        Log.i("myLogs", "x: " + x + ", y: " + y + ", scanner_capacity: " + scanner_capacity + ", sumXY: " + sumxy);

        Query query = objectRef.whereLessThanOrEqualTo("sumXY", sumxy + scanner_capacity)
                .whereGreaterThanOrEqualTo("sumXY", sumxy - scanner_capacity);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    objectModelList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i("myLogs", "document.getId: " + document.getId());
                        ObjectModel objectModel = new ObjectModel();
                        objectModel.setName(document.getId());
                        objectModel.setType(document.getString("type"));

                        //  Distinguish between (2;8) and (3;7)
                        if (document.getLong("sumXY").intValue() == sumxy) {
                            objectModel.setDistance(Math.abs(document.getLong("x").intValue() - x));
                        } else {
                            objectModel.setDistance(Math.abs(document.getLong("sumXY").intValue() - sumxy));
                        }
                        objectModelList.add(objectModel);
                    }


                    Collections.sort(objectModelList, new Comparator<ObjectModel>() {
                        @Override
                        public int compare(ObjectModel objectModel, ObjectModel t1) {
                            return (objectModel.getDistance() - t1.getDistance());
                        }
                    });

                    ScanResultAdapter scanResultAdapter = new ScanResultAdapter(objectModelList, ScanResultActivity.this);
                    rvScanResult.setAdapter(scanResultAdapter);
                }
            }
        });


    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }

    @Override
    public void onItemClick(int pos) {

        Intent intent = new Intent(this, MoveActivity.class);
        intent.putExtra("x", x);
        intent.putExtra("y", y);
        intent.putExtra("sumXY", sumxy);
        intent.putExtra("hull", hull);
        intent.putExtra("cargo", cargo);
        intent.putExtra("scanner_capacity", scanner_capacity);
        intent.putExtra("shield", shield);
        intent.putExtra("ship", ship);
        intent.putExtra("position", position);
        // Show only the recycler view item which was clicked
        ObjectModel objectModel = objectModelList.get(pos);
        intent.putExtra("objectType", objectModel.getType());
        intent.putExtra("objectName", objectModel.getName());
        intent.putExtra("objectDistance", objectModel.getDistance());
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
