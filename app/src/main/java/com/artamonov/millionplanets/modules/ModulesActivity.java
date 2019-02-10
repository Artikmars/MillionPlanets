package com.artamonov.millionplanets.modules;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.artamonov.millionplanets.R;

public class ModulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules);
    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }
}
