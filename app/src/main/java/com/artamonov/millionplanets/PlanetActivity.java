package com.artamonov.millionplanets;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.artamonov.millionplanets.market.MarketActivity;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.artamonov.millionplanets.sectors.SectorsActivity;
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
                    userList.setMoveToObjectName(doc.getString("moveToObjectName"));
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
        tvSize = findViewById(R.id.planet_size);
        tvSectors = findViewById(R.id.planet_sectors);
        tvFuel = findViewById(R.id.planet_user_fuel);


    }

    public void onGetFuel(View view) {
        documentReference.update("fuel", 20);
    }

    public void onTakeOff(View view) {
        finish();
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
}
