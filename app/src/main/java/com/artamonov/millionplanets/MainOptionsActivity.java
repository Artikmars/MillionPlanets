package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainOptionsActivity extends AppCompatActivity {

    Button btnScan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_options);
        btnScan = findViewById(R.id.scan);
    }

    public void onScan(View view) {
        startActivity(new Intent(getApplicationContext(), ScanActivity.class));
    }
}
