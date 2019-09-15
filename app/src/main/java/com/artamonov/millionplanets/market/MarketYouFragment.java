package com.artamonov.millionplanets.market;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import java.util.ArrayList;
import java.util.List;

public class MarketYouFragment extends Fragment implements MarketYouAdapter.DialogListener {

    RecyclerView rvScanResult;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private MarketPagerAdapter mSectionsPagerAdapter;
    // User userList = new User();
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private TextView tvPosition;
    private TextView tvShip;
    private TextView tvHp;
    private TextView tvShield;
    private TextView tvCargo;
    private TextView tvFuel;
    private TextView tv_ScannerCapacity;
    private FirebaseUser firebaseUser;
    private View parentLayout;
    private DocumentReference documentReferenceInventory;
    private RecyclerView rvMarketYou;
    private DocumentReference documentReferenceUser;
    private DocumentReference documentReferencePlanetMarket;
    private ObjectModel objectModel;

    public MarketYouFragment() {}

    public static MarketYouFragment newInstance() {
        MarketYouFragment fragment = new MarketYouFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

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
                                    /* Map<String, Object> documentSnapshot = transaction.get(documentReferenceInventory).getData();
                                    ObjectModel objectModel = new ObjectModel();
                                    for (Map.Entry<String, Object> entry : documentSnapshot.entrySet()) {
                                        objectModel.setResourceName(entry.getKey());
                                        Log.i("myTags", "apply: objectModel.setResourceName(entry.getKey());: " +
                                                objectModel.getResourceName());
                                    }*/
                                    objectModel = new ObjectModel();
                                    DocumentSnapshot documentSnapshotInventory =
                                            transaction.get(documentReferenceInventory);
                                    User user = new User();
                                    user.setResource_iron(
                                            documentSnapshotInventory.getLong("Iron").intValue());
                                    Log.i(
                                            "myTags",
                                            "apply: user.getIron: " + user.getResource_iron());
                                    DocumentSnapshot documentSnapshot1 =
                                            transaction.get(documentReferenceUser);
                                    user.setMoveToObjectName(
                                            documentSnapshot1.getString("moveToObjectName"));
                                    user.setMoney(documentSnapshot1.getLong("money").intValue());
                                    Log.i(
                                            "myTags",
                                            "apply: user.getName: " + user.getMoveToObjectName());
                                    documentReferencePlanetMarket =
                                            firebaseFirestore
                                                    .collection("Objects")
                                                    .document(user.getMoveToObjectName());
                                    DocumentSnapshot documentSnapshot2 =
                                            transaction.get(documentReferencePlanetMarket);
                                    objectModel.setPrice_buy_iron(
                                            documentSnapshot2.getLong("price_buy_iron").intValue());
                                    objectModel.setPrice_sell_iron(
                                            documentSnapshot2
                                                    .getLong("price_sell_iron")
                                                    .intValue());
                                    objectModel.setIronAmount(
                                            documentSnapshot2.getLong("iron").intValue());
                                    Log.i(
                                            "myTags",
                                            "apply: setPrice_buy_iron: "
                                                    + objectModel.getPrice_buy_iron());
                                    Log.i(
                                            "myTags",
                                            "apply: setPrice_sell_iron: "
                                                    + objectModel.getPrice_sell_iron());
                                    userList = new ArrayList<>();
                                    userList.add(user);
                                    objectModelList = new ArrayList<>();
                                    objectModelList.add(objectModel);
                                    transaction.update(
                                            documentReferenceInventory,
                                            "Iron",
                                            user.getResource_iron());
                                    transaction.update(
                                            documentReferenceUser,
                                            "moveToObjectName",
                                            user.getMoveToObjectName());
                                    transaction.update(
                                            documentReferencePlanetMarket,
                                            "price_buy_iron",
                                            objectModel.getPrice_buy_iron());
                                    return null;
                                }
                            })
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    setAdapter();
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
        MarketYouAdapter marketYouAdapter =
                new MarketYouAdapter(userList, objectModelList, getActivity(), this);
        marketYouAdapter.notifyDataSetChanged();
        rvMarketYou.setAdapter(marketYouAdapter);
        rvMarketYou.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i("myTags", "userList size: " + userList.size());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_you, container, false);
        rvMarketYou = view.findViewById(R.id.rvMarketYou);
        Button btnAction = view.findViewById(R.id.market_action_btn);
        btnAction.setVisibility(View.VISIBLE);
        btnAction.setText(getResources().getString(R.string.sectors_action_sell));
        btnAction.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseFirestore.runTransaction(
                                new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction)
                                            throws FirebaseFirestoreException {
                                        transaction.update(documentReferenceInventory, "Iron", 0);
                                        transaction.update(
                                                documentReferenceUser,
                                                "money",
                                                userList.get(0).getMoney()
                                                        + userList.get(0).getResource_iron()
                                                                * objectModel.getPrice_buy_iron());
                                        transaction.update(
                                                documentReferencePlanetMarket,
                                                "iron",
                                                objectModel.getIronAmount()
                                                        + userList.get(0).getResource_iron());
                                        return null;
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
