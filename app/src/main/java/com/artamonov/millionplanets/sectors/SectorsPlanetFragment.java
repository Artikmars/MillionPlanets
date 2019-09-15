package com.artamonov.millionplanets.sectors;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.artamonov.millionplanets.R;
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

public class SectorsPlanetFragment extends Fragment implements SectorsPlanetAdapter.DialogListener {

    FirebaseFirestore firebaseFirestore;
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReferenceInventory;
    private RecyclerView rvSectors;
    private TextView btnAction;
    private DocumentReference documentReferenceUser;

    public SectorsPlanetFragment() {}

    static SectorsPlanetFragment newInstance() {
        SectorsPlanetFragment fragment = new SectorsPlanetFragment();
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
            btnAction.setText(getResources().getString(R.string.sectors_action_get));

            Log.i("myTags", "setUserVisibleHint ");
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            documentReferenceInventory =
                    firebaseFirestore
                            .collection("Inventory")
                            .document(firebaseUser.getDisplayName());
            documentReferenceUser =
                    firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());

            firebaseFirestore
                    .runTransaction(
                            new Transaction.Function<Void>() {

                                @Override
                                public Void apply(Transaction transaction)
                                        throws FirebaseFirestoreException {
                                    ObjectModel objectModel = new ObjectModel();
                                    User user = new User();
                                    DocumentSnapshot documentReferenceUserSnapshot =
                                            transaction.get(documentReferenceUser);
                                    user.setMoveToObjectName(
                                            documentReferenceUserSnapshot.getString(
                                                    "moveToObjectName"));
                                    user.setMoney(
                                            documentReferenceUserSnapshot
                                                    .getLong("money")
                                                    .intValue());

                                    DocumentSnapshot documentReferenceInventorySnapshot =
                                            transaction.get(documentReferenceInventory);
                                    user.setSectors(
                                            documentReferenceInventorySnapshot
                                                    .getLong(user.getMoveToObjectName())
                                                    .intValue());

                                    DocumentReference documentReferencePlanetMarket =
                                            firebaseFirestore
                                                    .collection("Objects")
                                                    .document(user.getMoveToObjectName());
                                    DocumentSnapshot documentReferencePlanetMarketSnapshot =
                                            transaction.get(documentReferencePlanetMarket);
                                    Log.i(
                                            "myTags",
                                            "apply in Planet: documentReferencePlanetMarket: "
                                                    + documentReferencePlanetMarket.toString());
                                    objectModel.setPlanetSectors(
                                            documentReferencePlanetMarketSnapshot
                                                    .getLong("sectors")
                                                    .intValue());
                                    objectModel.setPlanetSectorsPrice(
                                            documentReferencePlanetMarketSnapshot
                                                    .getLong("sectors_price")
                                                    .intValue());
                                    Log.i(
                                            "myTags",
                                            "apply in Planet: sectors: "
                                                    + objectModel.getPlanetSectors());
                                    Log.i(
                                            "myTags",
                                            "apply in Planet: sectors_price: "
                                                    + objectModel.getPlanetSectorsPrice());
                                    userList = new ArrayList<>();
                                    userList.add(user);
                                    objectModelList = new ArrayList<>();
                                    objectModelList.add(objectModel);
                                    transaction.update(
                                            documentReferenceInventory,
                                            user.getMoveToObjectName(),
                                            user.getSectors());
                                    transaction.update(
                                            documentReferenceUser,
                                            "moveToObjectName",
                                            user.getMoveToObjectName());
                                    transaction.update(
                                            documentReferencePlanetMarket,
                                            "sectors",
                                            objectModel.getPlanetSectors());
                                    return null;
                                }
                            })
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    setAdapter();
                                    if (userList.get(0).getSectors() == 2
                                            || objectModelList.get(0).getPlanetSectors() == 0) {
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
        SectorsPlanetAdapter sectorsPlanetAdapter =
                new SectorsPlanetAdapter(userList, objectModelList, this);
        sectorsPlanetAdapter.notifyDataSetChanged();
        rvSectors.setAdapter(sectorsPlanetAdapter);
        rvSectors.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i("myTags", "userList size: " + userList.size());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sectors, container, false);
        Log.i("myTags", "ON CREATE VIEW - PLANET ");
        rvSectors = view.findViewById(R.id.rvSectors);
        btnAction = view.findViewById(R.id.sections_action_btn);
        btnAction.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (userList.get(0).getMoney()
                                < 2 * objectModelList.get(0).getPlanetSectorsPrice()) {
                            Snackbar.make(
                                            getActivity().findViewById(android.R.id.content),
                                            "You don't have enough money to buy 2 sectors! Come again",
                                            Snackbar.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        if (userList.get(0).getSectors() == 0) {
                            SectorsPlanetDialog sectorsPlanetDialog = new SectorsPlanetDialog();
                            sectorsPlanetDialog.show(getFragmentManager(), "text");
                            return;
                        }

                        firebaseFirestore
                                .runTransaction(
                                        new Transaction.Function<Void>() {
                                            @Nullable
                                            @Override
                                            public Void apply(@NonNull Transaction transaction)
                                                    throws FirebaseFirestoreException {

                                                DocumentReference documentReferencePlanetMarket =
                                                        firebaseFirestore
                                                                .collection("Objects")
                                                                .document(
                                                                        userList.get(0)
                                                                                .getMoveToObjectName());
                                                transaction.update(
                                                        documentReferenceInventory,
                                                        userList.get(0).getMoveToObjectName(),
                                                        userList.get(0).getSectors() + 1);
                                                transaction.update(
                                                        documentReferenceUser,
                                                        "money",
                                                        userList.get(0).getMoney()
                                                                - objectModelList
                                                                        .get(0)
                                                                        .getPlanetSectorsPrice());
                                                transaction.update(
                                                        documentReferencePlanetMarket,
                                                        "sectors",
                                                        objectModelList.get(0).getPlanetSectors()
                                                                - 1);
                                                return null;
                                            }
                                        })
                                .addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(
                                                                getActivity()
                                                                        .findViewById(
                                                                                android.R
                                                                                        .id
                                                                                        .content),
                                                                "Congratulations! You bought 1 sector! Come later to farm!",
                                                                Snackbar.LENGTH_LONG)
                                                        .show();
                                                btnAction.setEnabled(false);
                                                btnAction.setBackgroundColor(
                                                        getResources().getColor(R.color.grey));
                                            }
                                        });
                    }
                });
        return view;
    }

    @Override
    public void onDialogCreate() {}
}
