package com.artamonov.millionplanets;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.artamonov.millionplanets.market.MarketActivity;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.artamonov.millionplanets.modules.ModulesListActivity;
import com.artamonov.millionplanets.sectors.SectorsActivity;
import com.artamonov.millionplanets.shipyard.ShipyardActivity;
import com.artamonov.millionplanets.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.appcompat.app.AppCompatActivity;

public class PlanetActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    User userList = new User();
    ObjectModel objectModelList = new ObjectModel();
    private DocumentReference documentReference;
    private DocumentReference planetDocumentReference;
    private FirebaseUser firebaseUser;
    private TextView tvClass;
    private TextView tvSize;
    private TextView tvSectors;
    private TextView tvFuel;
    private TextView tvMoney;
    private TextView btnGetFuel;
    private TextView btnMarket;
    private TextView btnSectors;
    private TextView btnShipyard;


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
                    userList.setFuel(doc.getLong("fuel").intValue());
                    userList.setMoney(doc.getLong("money").intValue());
                    userList.setMoveToObjectName(doc.getString("moveToObjectName"));
                    userList.setShip(doc.getString("ship"));
                    tvFuel.setText(Integer.toString(userList.getFuel()));

                    planetDocumentReference = firebaseFirestore.collection("Objects")
                            .document(userList.getMoveToObjectName());
                    planetDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            objectModelList.setPlanetClass(documentSnapshot.getString("class"));
                            objectModelList.setPlanetSize(documentSnapshot.getString("size"));
                            objectModelList.setPlanetSectors(documentSnapshot.getLong("sectors").intValue());
                            tvClass.setText(objectModelList.getPlanetClass());
                            tvSectors.setText(Integer.toString(objectModelList.getPlanetSectors()));
                            tvSize.setText(objectModelList.getPlanetSize());
                            tvMoney.setText(Integer.toString(userList.getMoney()));

                            setObjectsAccessLevel();
                        }

                        private void setObjectsAccessLevel() {

                            double occupationLevel = (double) (6 - objectModelList.getPlanetSectors()) / 6;

                            if (occupationLevel < 0.25) {
                                Log.i("myTags", "objectModelList.getPlanetSectors: < 0.25 - " + occupationLevel);
                                btnGetFuel.setBackgroundColor(getResources().getColor(R.color.grey));
                                btnGetFuel.setEnabled(false);
                                btnMarket.setBackgroundColor(getResources().getColor(R.color.grey));
                                btnMarket.setEnabled(false);
                                btnShipyard.setEnabled(false);
                                btnShipyard.setBackgroundColor(getResources().getColor(R.color.grey));

                            }
                            if (occupationLevel >= 0.25 && occupationLevel < 0.5) {
                                Log.i("myTags", "objectModelList.getPlanetSectors: 0.25-0.5 - " + occupationLevel);
                                btnMarket.setBackgroundColor(getResources().getColor(R.color.grey));
                                btnMarket.setEnabled(false);
                                btnGetFuel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                btnGetFuel.setEnabled(true);
                                btnShipyard.setEnabled(false);
                                btnShipyard.setBackgroundColor(getResources().getColor(R.color.grey));
                            }
                            if (occupationLevel >= 0.5 && occupationLevel < 0.75) {
                                Log.i("myTags", "objectModelList.getPlanetSectors:  0.5 more - " + occupationLevel);
                                btnMarket.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                btnMarket.setEnabled(true);
                                btnGetFuel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                btnGetFuel.setEnabled(true);
                                btnShipyard.setEnabled(false);
                                btnShipyard.setBackgroundColor(getResources().getColor(R.color.grey));
                            }
                            if (occupationLevel >= 0.75) {
                                Log.i("myTags", "objectModelList.getPlanetSectors:  0.5 more - " + occupationLevel);
                                btnMarket.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                btnMarket.setEnabled(true);
                                btnGetFuel.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                btnGetFuel.setEnabled(true);
                                btnShipyard.setEnabled(true);
                                btnShipyard.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            }

                          /*  if (objectModelList.getPlanetSectors() / 6 < 0.75) {
                                Log.i("myTags", "objectModelList.getPlanetSectors: " + objectModelList.getPlanetSectors());
                            }*/


                        }

                    });

                }
            }

        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planet);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser.getDisplayName());

        tvClass = findViewById(R.id.planet_class);
        tvSize = findViewById(R.id.shipyard_hp);
        tvSectors = findViewById(R.id.planet_sectors);
        tvFuel = findViewById(R.id.planet_fuel);
        tvMoney = findViewById(R.id.planet_money);
        btnGetFuel = findViewById(R.id.planet_get_fuel);
        btnMarket = findViewById(R.id.planet_market);
        btnSectors = findViewById(R.id.planet_btn_sectors);
        btnShipyard = findViewById(R.id.planet_shipyard);


    }

    public void onGetFuel(View view) {
        Integer fuelToFill = Utils.getShipFuelInfo(userList.getShip()) - userList.getFuel();
        Integer price = fuelToFill * 1000;
        if (userList.getMoney() >= price) {
            documentReference.update("fuel", Utils.getShipFuelInfo(userList.getShip()));
            documentReference.update("money", userList.getMoney() - price);
        }
    }

    public void onTakeOff(View view) {
        startActivity(new Intent(this, MainOptionsActivity.class));
    }


    public void onGoToMarket(View view) {
        Intent intent = new Intent(this, MarketActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void onGoToSectors(View view) {
        Intent intent = new Intent(this, SectorsActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void onGoToShipyard(View view) {
        Intent intent = new Intent(this, ShipyardActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }

    }

    public void onGoToModules(View view) {
        Intent intent = new Intent(this, ModulesListActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }
}
