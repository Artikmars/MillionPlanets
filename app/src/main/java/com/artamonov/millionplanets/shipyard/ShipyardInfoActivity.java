package com.artamonov.millionplanets.shipyard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.artamonov.millionplanets.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ShipyardInfoActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    User userList = new User();
    ObjectModel objectModelList = new ObjectModel();
    /*  User figher = new User();
      User trader = new User();
      User rs = new User();*/
    List<User> shipsList = new ArrayList<>();

    private DocumentReference documentReference;
    private DocumentReference planetDocumentReference;
    private FirebaseUser firebaseUser;
    private TextView tv_YourShip;
    private TextView tvUserCash;
    private TextView tvUserBank;
    private Button btnBuy;
    private RecyclerView rvShipyard;
    private List<String> shipsArrayList = new ArrayList<>();
    private String yourShip;
    private User shipToBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipyard_info);
        Intent intent = getIntent();
        Integer position = intent.getIntExtra("position", 0);
        yourShip = intent.getStringExtra("your_ship");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser.getDisplayName());


        TextView tvHp = findViewById(R.id.shipyard_hp);
        TextView tvCargo = findViewById(R.id.shipyard_cargo);
        TextView tvShield = findViewById(R.id.shipyard_shield);
        TextView tvFuel = findViewById(R.id.shipyard_fuel);
        TextView tvJump = findViewById(R.id.shipyard_jump);
        TextView tvCost = findViewById(R.id.shipyard_cost);
        TextView tvWeaponSlots = findViewById(R.id.shipyard_weapon_slots);
        TextView tv_ScannerCapacity = findViewById(R.id.shipyard_scanner);
        tv_YourShip = findViewById(R.id.shipyard_your_ship);
        TextView tvShipyardName = findViewById(R.id.shipyard_name);
        TextView tvClass = findViewById(R.id.shipyard_class);
        btnBuy = findViewById(R.id.shipyard_btn_buy);
        tvUserCash = findViewById(R.id.shipyardinfo_user_cash);
        tvUserBank = findViewById(R.id.shipyardinfo_user_bank);


        shipToBuy = Utils.getCurrentShipInfo(position);
        setOnBuyButtonVisibility(shipToBuy);
        tvHp.setText(String.valueOf(shipToBuy.getHp()));
        tvCargo.setText(String.valueOf(shipToBuy.getCargo()));
        tvShield.setText(String.valueOf(shipToBuy.getShield()));
        tvFuel.setText(String.valueOf(shipToBuy.getFuel()));
        tvJump.setText(String.valueOf(shipToBuy.getJump()));
        tvCost.setText(String.valueOf(shipToBuy.getShipPrice()));
        tvWeaponSlots.setText(String.valueOf(shipToBuy.getWeaponSlots()));
        tv_ScannerCapacity.setText(String.valueOf(shipToBuy.getScanner_capacity()));
        tvShipyardName.setText(shipToBuy.getShip());
        tvClass.setText(shipToBuy.getShipClass());


    }

    private void showDiffStats(User shipToBuy, User userList) {
        if (shipToBuy.getShip().equals(userList.getShip())) {
            return;
        }
        TextView tvHpDiff = findViewById(R.id.shipyard_hp_diff);
        TextView tvCargoDiff = findViewById(R.id.shipyard_cargo_diff);
        TextView tvShieldDiff = findViewById(R.id.shipyard_shield_diff);
        TextView tvFuelDiff = findViewById(R.id.shipyard_fuel_diff);
        TextView tvJumpDiff = findViewById(R.id.shipyard_jump_diff);
        TextView tvCostDiff = findViewById(R.id.shipyard_cost_diff);
        TextView tvWeaponSlotsDiff = findViewById(R.id.shipyard_weapon_slots_diff);
        TextView tvScannerCapacityDiff = findViewById(R.id.shipyard_scanner_diff);
        int hpDiff = shipToBuy.getHp() - userList.getHp();
        if (hpDiff >= 0) {
            tvHpDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvHpDiff.setText("+ " + hpDiff);
        } else {
            tvHpDiff.setTextColor(getResources().getColor(R.color.red));
            tvHpDiff.setText(String.valueOf(hpDiff));
        }

        int cargoDiff = shipToBuy.getCargo() - userList.getCargo();
        if (cargoDiff >= 0) {
            tvCargoDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvCargoDiff.setText("+ " + cargoDiff);
        } else {
            tvCargoDiff.setTextColor(getResources().getColor(R.color.red));
            tvCargoDiff.setText(String.valueOf(cargoDiff));
        }
        int shieldDiff = shipToBuy.getShield() - userList.getShield();
        if (shieldDiff >= 0) {
            tvShieldDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvShieldDiff.setText("+ " + shieldDiff);
        } else {
            tvShieldDiff.setTextColor(getResources().getColor(R.color.red));
            tvShieldDiff.setText(String.valueOf(shieldDiff));
        }
        int fuelDiff = shipToBuy.getFuel() - userList.getFuel();
        if (fuelDiff >= 0) {
            tvFuelDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvFuelDiff.setText("+ " + fuelDiff);
        } else {
            tvFuelDiff.setTextColor(getResources().getColor(R.color.red));
            tvFuelDiff.setText(String.valueOf(fuelDiff));
        }
        int jumpDiff = shipToBuy.getJump() - userList.getJump();
        if (jumpDiff >= 0) {
            tvJumpDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvJumpDiff.setText("+ " + jumpDiff);
        } else {
            tvJumpDiff.setTextColor(getResources().getColor(R.color.red));
            tvJumpDiff.setText(String.valueOf(jumpDiff));
        }
        int costDiff = shipToBuy.getShipPrice() - userList.getShipPrice();
        if (costDiff >= 0) {
            tvCostDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvCostDiff.setText("+ " + costDiff);
        } else {
            tvCostDiff.setTextColor(getResources().getColor(R.color.red));
            tvCostDiff.setText(String.valueOf(costDiff));
        }
        int weaponSlotsDiff = shipToBuy.getWeaponSlots() - userList.getWeaponSlots();
        if (weaponSlotsDiff >= 0) {
            tvWeaponSlotsDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvWeaponSlotsDiff.setText("+ " + weaponSlotsDiff);
        } else {
            tvWeaponSlotsDiff.setTextColor(getResources().getColor(R.color.red));
            tvWeaponSlotsDiff.setText(String.valueOf(weaponSlotsDiff));
        }
        int scannerDiff = shipToBuy.getScanner_capacity() - userList.getScanner_capacity();
        if (scannerDiff >= 0) {
            tvScannerCapacityDiff.setTextColor(getResources().getColor(R.color.colorAccent));
            tvScannerCapacityDiff.setText("+ " + scannerDiff);
        } else {
            tvScannerCapacityDiff.setTextColor(getResources().getColor(R.color.red));
            tvScannerCapacityDiff.setText(String.valueOf(scannerDiff));
        }
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
                    userList.setHp(doc.getLong("hp").intValue());
                    userList.setCargo(doc.getLong("cargo").intValue());
                    userList.setFuel(doc.getLong("fuel").intValue());
                    userList.setScanner_capacity(doc.getLong("scanner_capacity").intValue());
                    userList.setShield(doc.getLong("shield").intValue());
                    userList.setJump(doc.getLong("jump").intValue());
                    userList.setShipPrice(doc.getLong("shipPrice").intValue());
                    userList.setWeaponSlots(doc.getLong("weaponSlots").intValue());
                    userList.setShip(doc.getString("ship"));
                    userList.setShipPrice(doc.getLong("shipPrice").intValue());
                    userList.setMoney(doc.getLong("money").intValue());
                    tv_YourShip.setText(userList.getShip());
                    tvUserCash.setText(Integer.toString(userList.getMoney()));
                    showDiffStats(shipToBuy, userList);
                }
            }

        });

    }

    private void setOnBuyButtonVisibility(User ship) {
        if (yourShip.equals(ship.getShip())) {
            btnBuy.setEnabled(false);
            btnBuy.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }

    public void onBuy(View view) {

        if (!ifEnoughMoney()) {
            Toast.makeText(this, "Not enough money to buy the ship!", Toast.LENGTH_SHORT).show();
            return;
        }
        btnBuy.setEnabled(false);
        btnBuy.setBackgroundColor(getResources().getColor(R.color.grey));
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Map<String, Object> shipMap = new HashMap<>();
                shipMap.put("hp", shipToBuy.getHp());
                shipMap.put("shield", shipToBuy.getShield());
                shipMap.put("cargo", shipToBuy.getCargo());
                shipMap.put("jump", shipToBuy.getJump());
                shipMap.put("fuel", shipToBuy.getFuel());
                shipMap.put("scanner_capacity", shipToBuy.getScanner_capacity());
                shipMap.put("weaponSlots", shipToBuy.getWeaponSlots());
                shipMap.put("ship", shipToBuy.getShip());
                shipMap.put("shipClass", shipToBuy.getShipClass());
                shipMap.put("shipPrice", shipToBuy.getShipPrice());
                shipMap.put("money", userList.getMoney() - shipToBuy.getShipPrice() + userList.getShipPrice() / 2);
                transaction.update(documentReference, shipMap);
                return null;
            }
        });
    }

    private boolean ifEnoughMoney() {
        return userList.getMoney() + (userList.getShipPrice() / 2) >= shipToBuy.getShipPrice();
    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }
}
