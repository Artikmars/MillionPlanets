package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.artamonov.millionplanets.adapter.ScanResultAdapter;
import com.artamonov.millionplanets.model.ObjectModel;

import java.util.ArrayList;
import java.util.List;

public class MoveActivity extends AppCompatActivity {

    RecyclerView rvScanResult;
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHull;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tv_ScannerCapacity;
    private Integer x;
    private Integer y;
    private Integer sumxy;
    private String cargo;
    private String hull;
    private String shield;
    private String ship;
    private String position;
    private Integer scanner_capacity;
    private String objectName;
    private String objectType;
    private int objectDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move);

        tvPosition = findViewById(R.id.move_coordinates);
        tvShip = findViewById(R.id.move_ship);
        tvHull = findViewById(R.id.move_hull);
        tvShield = findViewById(R.id.move_shield);
        tvCargo = findViewById(R.id.move_cargo);
        tv_ScannerCapacity = findViewById(R.id.move_scanner_capacity);
        rvScanResult = findViewById(R.id.move_scan_result_list);
        rvScanResult.setLayoutManager(new LinearLayoutManager(this));


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
        objectName = intent.getStringExtra("objectName");
        objectType = intent.getStringExtra("objectType");
        objectDistance = intent.getIntExtra("objectDistance", 0);


        tvPosition.setText(position);
        tvShip.setText(ship);
        tvHull.setText(hull);
        tvShield.setText(shield);
        tvCargo.setText(cargo);
        tv_ScannerCapacity.setText(String.valueOf(scanner_capacity));


        List<ObjectModel> objectModelList = new ArrayList<>();
        ObjectModel objectModel = new ObjectModel();
        objectModel.setType(objectType);
        objectModel.setName(objectName);
        objectModel.setDistance(objectDistance);
        objectModelList.add(objectModel);

        ScanResultAdapter scanResultAdapter = new ScanResultAdapter(objectModelList);
        rvScanResult.setAdapter(scanResultAdapter);


    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }

}
