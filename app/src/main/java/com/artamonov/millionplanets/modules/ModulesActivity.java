package com.artamonov.millionplanets.modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.Module;
import com.artamonov.millionplanets.model.User;
import com.artamonov.millionplanets.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModulesActivity extends AppCompatActivity implements ModulesAdapter.ItemClickListener {
    FirebaseUser firebaseUser;
    DocumentReference documentReference;
    DocumentReference modulesRef;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    User userList = new User();
    RecyclerView rvModules;
    private TextView tvMoney;
    private List<Module> modules;
    private int existedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules);
        tvMoney = findViewById(R.id.modules_user_cash);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser.getDisplayName());

        rvModules = findViewById(R.id.rvModules);
        rvModules.setLayoutManager(new LinearLayoutManager(this));
        modules = new ArrayList<>();
        modules.add(0, new Module("Light Laser", 1, 0));
        modules.add(1, new Module("Medium Laser", 2, 5000));
        modules.add(2, new Module("Heavy Laser", 3, 10000));
        modules.add(3, new Module("Military Laser", 2, 0));
        modules.add(4, new Module("Heavy Military Laser", 3, 0));
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
                    userList.setMoney(doc.getLong("money").intValue());
                    tvMoney.setText(Integer.toString(userList.getMoney()));
                }
            }
        });
        modulesRef = firebaseFirestore.collection("Modules")
                .document(firebaseUser.getDisplayName());
        modulesRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot doc, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (doc.exists()) {
                    // Weapon which is already installed on a s
                    existedItem = Utils.getWeaponIdByName(doc.getString("weaponName"));
                }
                ModulesAdapter modulesAdapter = new ModulesAdapter(modules, existedItem,
                        getApplicationContext(), ModulesActivity.this);
                rvModules.setAdapter(modulesAdapter);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        //Last two weapon types are currently unavailable
        if (position == 3 || position == 4) {
            return;
        }
        Intent intent = new Intent(this, ModulesInfoActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }
}
