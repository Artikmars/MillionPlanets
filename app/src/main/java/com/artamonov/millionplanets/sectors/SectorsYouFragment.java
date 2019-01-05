package com.artamonov.millionplanets.sectors;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.market.MarketYouDialog;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SectorsYouFragment extends Fragment implements SectorsYouAdapter.DialogListener {


    FirebaseFirestore firebaseFirestore;
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReferenceInventory;
    private RecyclerView rvSectors;
    private TextView btnAction;
    private DocumentReference documentReferenceUser;

    public SectorsYouFragment() {
    }

    static SectorsYouFragment newInstance() {
        SectorsYouFragment fragment = new SectorsYouFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setButtonEnabled(boolean isEnabled) {
        if (isEnabled) {
            btnAction.setEnabled(true);
            btnAction.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            btnAction.setEnabled(false);
            btnAction.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.i("myTags", "setUserVisibleHint ");
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            documentReferenceInventory = firebaseFirestore.collection("Inventory")
                    .document(firebaseUser.getDisplayName());
            documentReferenceUser = firebaseFirestore.collection("Objects")
                    .document(firebaseUser.getDisplayName());

            firebaseFirestore.runTransaction(new Transaction.Function<Void>() {

                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                    ObjectModel objectModel = new ObjectModel();
                    User user = new User();


                    DocumentSnapshot documentSnapshot1 = transaction.get(documentReferenceUser);
                    user.setMoveToObjectName(documentSnapshot1.getString("moveToObjectName"));
                    user.setMoney(documentSnapshot1.getLong("money").intValue());
                    Log.i("myTags", "apply: user.getName: " + user.getMoveToObjectName());
                    DocumentSnapshot documentSnapshotInventory = transaction.get(documentReferenceInventory);
                    user.setSectors(documentSnapshotInventory.getLong(user.getMoveToObjectName()).intValue());
                    DocumentReference documentReferencePlanetMarket = firebaseFirestore.collection("Objects")
                            .document(user.getMoveToObjectName());
                    DocumentSnapshot documentSnapshot2 = transaction.get(documentReferencePlanetMarket);
                    objectModel.setPlanetSectorsPrice(documentSnapshot2.getLong("sectors_price").intValue());
                    objectModel.setPlanetSectors(documentSnapshot2.getLong("sectors").intValue());
                    Log.i("myTags", "apply: setPrice_buy_iron: " + objectModel.getPrice_buy_iron());
                    Log.i("myTags", "apply: setPrice_sell_iron: " + objectModel.getPrice_sell_iron());
                    userList = new ArrayList<>();
                    userList.add(user);
                    objectModelList = new ArrayList<>();
                    objectModelList.add(objectModel);
                    transaction.update(documentReferenceInventory, user.getMoveToObjectName(), user.getSectors());
                    transaction.update(documentReferenceUser, "moveToObjectName", user.getMoveToObjectName());
                    transaction.update(documentReferencePlanetMarket, "sectors", objectModel.getPlanetSectors());
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setAdapter();
                    if (userList.get(0).getSectors() == 0) {
                        setButtonEnabled(false);
                    } else {
                        setButtonEnabled(true);
                    }
                }
            });
         /*  documentReferenceInventory.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = new User();
                    user.setResource_iron(documentSnapshot.getLong("iron").intValue());
                    userList = new ArrayList<>();
                    userList.add(user);
                    setAdapter();


                }
            });*/
        }
    }

    private void setAdapter() {
        SectorsYouAdapter sectorsYouAdapter = new SectorsYouAdapter(userList, objectModelList, getActivity(), this);
        sectorsYouAdapter.notifyDataSetChanged();
        rvSectors.setAdapter(sectorsYouAdapter);
        rvSectors.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i("myTags", "userList size: " + userList.size());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sectors, container, false);
        Log.i("myTags", "ON CREATE VIEW: YOU: ");
        rvSectors = view.findViewById(R.id.rvSectors);
        btnAction = view.findViewById(R.id.sections_action_btn);
        btnAction.setText(getResources().getString(R.string.sectors_action_sell));
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userList.get(0).getSectors() == 2) {
                    SectorsYouDialog sectorsYouDialog = new SectorsYouDialog();
                    sectorsYouDialog.show(getFragmentManager(), "text");
                    return;
                }
                firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                        DocumentReference documentReferencePlanetMarket = firebaseFirestore.collection("Objects")
                                .document(userList.get(0).getMoveToObjectName());
                        transaction.update(documentReferenceInventory, userList.get(0).getMoveToObjectName(), 0);
                        transaction.update(documentReferenceUser, "money",
                                userList.get(0).getMoney() + objectModelList.get(0).getPlanetSectorsPrice() / 2);
                        transaction.update(documentReferencePlanetMarket, "sectors",
                                objectModelList.get(0).getPlanetSectors() + 1);
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                "You have sold 1 sector", Snackbar.LENGTH_LONG)
                                .show();
                        btnAction.setEnabled(false);
                        btnAction.setBackgroundColor(getResources().getColor(R.color.grey));
                    }
                });

            }

        });
        return view;
    }

    @Override
    public void onDialogCreate() {
        MarketYouDialog marketYouDialog = new MarketYouDialog();
        marketYouDialog.show(getFragmentManager(), "text");
    }
}
