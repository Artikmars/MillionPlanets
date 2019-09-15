package com.artamonov.millionplanets.modules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.Module;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.artamonov.millionplanets.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModulesInfoActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    User userList = new User();
    ObjectModel objectModelList = new ObjectModel();
    List<Module> moduleList = new ArrayList<>();

    private DocumentReference userDocumentReference;
    private DocumentReference modulesDocumentReference;
    private DocumentReference planetDocumentReference;
    private FirebaseUser firebaseUser;
    private TextView tv_YourShip;
    private TextView tvUserCash;
    private TextView tvUserBank;
    private Button btnBuy;
    private Button btnSell;
    private RecyclerView rvShipyard;
    private List<String> shipsArrayList = new ArrayList<>();
    private String yourShip;
    private Module module;
    private int position;
    private int freeSlots;
    private String currentModule;
    private ConstraintLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modules_info);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDocumentReference =
                firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        modulesDocumentReference =
                firebaseFirestore.collection("Modules").document(firebaseUser.getDisplayName());

        parentLayout = findViewById(R.id.modulesinfo_parentLayout);
        TextView tvClass = findViewById(R.id.modulesinfo_class);
        TextView tvCost = findViewById(R.id.modulesinfo_cost);
        TextView tvDamageHP = findViewById(R.id.modulesinfo_damageHp);
        TextView tvDamageShield = findViewById(R.id.modulesinfo_damageShield);
        tvUserCash = findViewById(R.id.modulesinfo_user_cash);
        btnBuy = findViewById(R.id.modulesinfo_buy);
        btnSell = findViewById(R.id.modulesinfo_sell);

        module = Utils.getCurrentModuleInfo(position);
        tvClass.setText(module.getModuleClass());
        tvCost.setText(String.valueOf(module.getPrice()));
        tvDamageHP.setText(String.valueOf(module.getDamageHP()));
        tvCost.setText(String.valueOf(module.getPrice()));
        tvDamageShield.setText(String.valueOf(module.getDamageShield()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDocumentReference =
                firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        modulesDocumentReference =
                firebaseFirestore.collection("Modules").document(firebaseUser.getDisplayName());
        userDocumentReference.addSnapshotListener(
                this,
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(
                            @javax.annotation.Nullable DocumentSnapshot doc,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (doc.exists()) {
                            userList.setMoney(doc.getLong("money").intValue());
                            tvUserCash.setText(Integer.toString(userList.getMoney()));
                        }
                    }
                });
        modulesDocumentReference.addSnapshotListener(
                this,
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(
                            @javax.annotation.Nullable DocumentSnapshot doc,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (doc.exists()) {
                            currentModule = doc.getString("weaponName");
                            Log.i("myLogs", "current Module " + currentModule);
                            setOnBuySellButtonVisibility(currentModule);
                        } else {
                            setOnBuySellButtonVisibility("");
                        }
                    }
                });
    }

    private void setOnBuySellButtonVisibility(String currentModule) {
        if (currentModule.equals(module.getName())) {
            btnBuy.setEnabled(false);
            btnBuy.setBackgroundColor(getResources().getColor(R.color.grey));
            btnSell.setEnabled(true);
            btnSell.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            btnBuy.setEnabled(true);
            btnBuy.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btnSell.setEnabled(false);
            btnSell.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }

    public void onBuy(View view) {

        if (!ifEnoughMoney()) {
            Toast.makeText(this, "Not enough money to buy the module!", Toast.LENGTH_SHORT).show();
            return;
        }

        WriteBatch batch = firebaseFirestore.batch();
        Map<String, Object> moduleMap = new HashMap<>();
        moduleMap.put("damageHP", module.getDamageHP());
        moduleMap.put("damageShield", module.getDamageShield());
        moduleMap.put("weaponClass", module.getModuleClass());
        moduleMap.put("weaponName", module.getName());
        batch.set(modulesDocumentReference, moduleMap);
        batch.update(userDocumentReference, "money", userList.getMoney() - module.getPrice());
        batch.commit()
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btnBuy.setEnabled(false);
                                btnBuy.setBackgroundColor(getResources().getColor(R.color.grey));
                                btnSell.setEnabled(true);
                                btnSell.setBackgroundColor(
                                        getResources().getColor(R.color.colorAccent));
                                Snackbar.make(
                                                parentLayout,
                                                "The item was successfully bought",
                                                Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
    }

    private boolean ifEnoughMoney() {
        return userList.getMoney() > module.getPrice();
    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }

    public void onSell(View view) {
        WriteBatch batch = firebaseFirestore.batch();
        Map<String, Object> moduleMap = new HashMap<>();
        moduleMap.put("damageHP", Utils.getCurrentModuleInfo(0).getDamageHP());
        moduleMap.put("damageShield", Utils.getCurrentModuleInfo(0).getDamageShield());
        moduleMap.put("weaponClass", Utils.getCurrentModuleInfo(0).getModuleClass());
        moduleMap.put("weaponName", Utils.getCurrentModuleInfo(0).getName());
        batch.set(modulesDocumentReference, moduleMap);
        batch.update(userDocumentReference, "money", userList.getMoney() + module.getPrice() / 2);
        batch.commit()
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btnBuy.setEnabled(true);
                                btnBuy.setBackgroundColor(
                                        getResources().getColor(R.color.colorAccent));
                                btnSell.setEnabled(false);
                                btnSell.setBackgroundColor(getResources().getColor(R.color.grey));
                                Snackbar.make(
                                                parentLayout,
                                                "The item was successfully sold",
                                                Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
    }
}
