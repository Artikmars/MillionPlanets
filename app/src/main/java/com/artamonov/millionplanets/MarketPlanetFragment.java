package com.artamonov.millionplanets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artamonov.millionplanets.adapter.MarketAdapter;
import com.artamonov.millionplanets.adapter.MarketPagerAdapter;
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
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MarketPlanetFragment extends Fragment implements MarketAdapter.DialogListener {

    RecyclerView rvScanResult;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private MarketPagerAdapter mSectionsPagerAdapter;
    //User userList = new User();
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
    private ObjectModel objectModel;
    private DocumentReference documentReferenceInventory;
    private RecyclerView rvMarketYou;

    public MarketPlanetFragment() {
    }

    public static MarketPlanetFragment newInstance() {
        MarketPlanetFragment fragment = new MarketPlanetFragment();
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
            documentReferenceInventory = firebaseFirestore.collection("Inventory")
                    .document(firebaseUser.getDisplayName());
            final DocumentReference documentReferenceUser = firebaseFirestore.collection("Objects")
                    .document(firebaseUser.getDisplayName());

            firebaseFirestore.runTransaction(new Transaction.Function<Void>() {

                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    Map<String, Object> documentSnapshot = transaction.get(documentReferenceInventory).getData();
                    ObjectModel objectModel = new ObjectModel();
                    for (Map.Entry<String, Object> entry : documentSnapshot.entrySet()) {
                        objectModel.setResourceName(entry.getKey());
                        Log.i("myTags", "apply: objectModel.setResourceName(entry.getKey());: " +
                                objectModel.getResourceName());
                    }
                    DocumentSnapshot documentSnapshot2 = transaction.get(documentReferenceInventory);
                    User user = new User();
                    user.setResource_iron(documentSnapshot2.getLong("Iron").intValue());

                    Log.i("myTags", "apply: user.getIron: " + user.getResource_iron());
                    DocumentSnapshot documentSnapshot1 = transaction.get(documentReferenceUser);
                    user.setMoveToObjectName(documentSnapshot1.getString("moveToObjectName"));
                    Log.i("myTags", "apply: user.getName: " + user.getMoveToObjectName());
                    DocumentReference documentReferencePlanetMarket = firebaseFirestore.collection("Objects")
                            .document(user.getMoveToObjectName());
                    DocumentSnapshot documentSnapshot3 = transaction.get(documentReferencePlanetMarket);
                    objectModel.setPrice_buy_iron(documentSnapshot3.getLong("price_buy_iron").intValue());
                    objectModel.setPrice_sell_iron(documentSnapshot3.getLong("price_sell_iron").intValue());
                    Log.i("myTags", "apply: setPrice_buy_iron: " + objectModel.getPrice_buy_iron());
                    Log.i("myTags", "apply: setPrice_sell_iron: " + objectModel.getPrice_sell_iron());
                    userList = new ArrayList<>();
                    userList.add(user);
                    objectModelList = new ArrayList<>();
                    objectModelList.add(objectModel);
                    transaction.update(documentReferenceInventory, "Iron", user.getResource_iron());
                    transaction.update(documentReferenceUser, "moveToObjectName", user.getMoveToObjectName());
                    transaction.update(documentReferencePlanetMarket, "price_buy_iron", objectModel.getPrice_buy_iron());
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        MarketAdapter marketPlanetAdapter = new MarketAdapter(userList, objectModelList, true, this);
        rvMarketYou.setAdapter(marketPlanetAdapter);
        rvMarketYou.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i("myTags", "userList size: " + userList.size());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_you, container, false);
        rvMarketYou = view.findViewById(R.id.rvMarketYou);
        return view;
    }

    @Override
    public void onDialogCreate() {
        MarketPlanetDialog marketPlanetDialog = new MarketPlanetDialog();
        marketPlanetDialog.show(getFragmentManager(), "text");
    }
}
