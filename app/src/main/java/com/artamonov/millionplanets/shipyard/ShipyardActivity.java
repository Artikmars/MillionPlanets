package com.artamonov.millionplanets.shipyard;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.base.BaseActivity;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.ArrayList;
import java.util.List;

public class ShipyardActivity extends BaseActivity implements ShipyardAdapter.ItemClickListener {
    User userList = new User();
    ObjectModel objectModelList = new ObjectModel();
    User figher = new User();
    User trader = new User();
    User rs = new User();
    List<User> shipsList = new ArrayList<>();

    private DocumentReference documentReference;
    private DocumentReference planetDocumentReference;
    private FirebaseUser firebaseUser;
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHp;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tvFuel;
    private TextView tvMoney;
    private TextView tv_ScannerCapacity;
    private RecyclerView rvShipyard;
    private List<String> shipsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipyard);
        documentReference =
                firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        rvShipyard = findViewById(R.id.rvShipyard);
        rvShipyard.setLayoutManager(new LinearLayoutManager(this));
        tvMoney = findViewById(R.id.shipyard_user_cash);

        /* figher.setHp(50);
        figher.setShield(100);
        figher.setCargo(100);
        figher.setJump(10);
        figher.setFuel(20);
        figher.setScanner_capacity(15);
        figher.setWeaponSlots(3);*/
        figher.setShipPrice(0);
        figher.setShip(getString(R.string.fighter));
        shipsList.add(figher);

        /*   trader.setHp(100);
        trader.setShield(50);
        trader.setCargo(150);
        trader.setJump(25);
        trader.setFuel(50);
        trader.setScanner_capacity(30);
        trader.setWeaponSlots(1);*/
        trader.setShipPrice(50000);
        trader.setShip(getString(R.string.trader));
        shipsList.add(trader);

        /*    rs.setHp(150);
        rs.setShield(50);
        rs.setCargo(75);
        rs.setJump(75);
        rs.setFuel(150);
        rs.setScanner_capacity(100);
        rs.setWeaponSlots(2);*/
        rs.setShipPrice(100000);
        rs.setShip(getString(R.string.research_spaceship));
        shipsList.add(rs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference =
                firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        documentReference.addSnapshotListener(
                this,
                new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(
                            @javax.annotation.Nullable DocumentSnapshot doc,
                            @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (doc.exists()) {
                            userList.setShip(doc.getString("ship"));
                            userList.setMoney(doc.getLong("money").intValue());
                            tvMoney.setText(Long.toString(userList.getMoney()));

                            // For larger amount of spaceships

                            /* Map<Integer, Object> shipsMap = new HashMap<>();
                            shipsMap.put(1, shipFighter);
                            shipsMap.put(2, shipTrader);
                            shipsMap.put(3, shipRS);*/

                            Log.i(
                                    "myLogs",
                                    " shipsList.get(0).getShip(): " + shipsList.get(0).getShip());
                            Log.i(
                                    "myLogs",
                                    " Integer.toString(shipsList.get(0).getShipPrice(): "
                                            + shipsList.get(0).getShipPrice());
                            Log.i(
                                    "myLogs",
                                    " shipsList.get(1).getShip():" + shipsList.get(1).getShip());
                            Log.i(
                                    "myLogs",
                                    " Integer.toString(shipsList.get(0).getShipPrice():: "
                                            + shipsList.get(1).getShipPrice());
                            Log.i(
                                    "myLogs",
                                    " shipsList.get(2).getShip():: " + shipsList.get(2).getShip());
                            Log.i(
                                    "myLogs",
                                    " Integer.toString(shipsList.get(0).getShipPrice():: "
                                            + (shipsList.get(2).getShipPrice()));

                            ShipyardAdapter shipyardAdapter =
                                    new ShipyardAdapter(shipsList, ShipyardActivity.this);
                            rvShipyard.setAdapter(shipyardAdapter);
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ShipyardInfoActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("your_ship", userList.getShip());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void onGoBackToMainOptions(View view) {
        finish();
    }
}
