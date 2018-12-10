package com.artamonov.millionplanets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import java.util.List;


public class ScanResultFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    List<ObjectModel> objectModelList;
    RecyclerView rvScanResult;
    Button btnGoToMainOptions;
    private Integer distance = 0;
    private Integer x = 0;
    private Integer y = 0;
    private Integer sumXY = 0;


    public ScanResultFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("myLogs", "ScanResultFragment onCreate: ");
        if (getArguments() != null) {
            distance = getArguments().getInt("distance");
            x = getArguments().getInt("x");
            y = getArguments().getInt("y");
            sumXY = getArguments().getInt("sumXY");
            Log.i("myLogs", "ScanResultFragment onCreate: ");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_result, container, false);
        Log.i("myLogs", "onCreateView");
        rvScanResult = view.findViewById(R.id.scan_result_list);
        rvScanResult.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("myLogs", "firebaseUser: " + firebaseUser.getEmail());
        CollectionReference objectRef = firebaseFirestore.collection("GeoData");
        //Query query = objectRef.whereLessThan("x", x + distance).whereGreaterThan("x", x - distance)
        //         .whereLessThan("y", y + distance).whereGreaterThan("y", y - distance);
        Log.i("myLogs", "x: " + x + ", y: " + y + ", distance: " + distance + ", sumXY: " + sumXY);

        Query query = objectRef.whereLessThanOrEqualTo("sumXY", sumXY + distance)
                .whereGreaterThanOrEqualTo("sumXY", sumXY - distance);
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
                        if (document.getLong("sumXY").intValue() == sumXY) {
                            objectModel.setDistance(Math.abs(document.getLong("x").intValue() - x));
                        } else {
                            objectModel.setDistance(Math.abs(document.getLong("sumXY").intValue() - sumXY));
                        }
                        objectModelList.add(objectModel);
                    }
                    //    Log.i("myLogs", "objectList: " + objectModelList.get(0).getName());
                    //ScanResultAdapter scanResultAdapter = new ScanResultAdapter(objectModelList, ge);
                    //  rvScanResult.setAdapter(scanResultAdapter);
                }
            }
        });

        btnGoToMainOptions = view.findViewById(R.id.back_to_menu);
        btnGoToMainOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*MainOptionsFragment mainOptionsFragment = new MainOptionsFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.main_options_fragment, mainOptionsFragment).commit();*/
                startActivity(new Intent(getActivity().getApplicationContext(), MainOptionsActivity.class));
            }
        });


        return view;
    }


    public void onGoBackToMainOptions(View view) {
    }

}
