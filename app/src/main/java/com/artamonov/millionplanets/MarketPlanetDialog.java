package com.artamonov.millionplanets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.artamonov.millionplanets.adapter.MarketYouAdapter;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MarketPlanetDialog extends AppCompatDialogFragment {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    User userList = new User();
    private NumberPicker numberPicker;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReferenceUser;
    private DocumentReference documentReferenceInventory;
    private MarketYouAdapter.DialogListener dialogListener;
    private boolean isPlanetTab;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (MarketYouAdapter.DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReferenceInventory = firebaseFirestore.collection("Inventory")
                .document(firebaseUser.getDisplayName());
        documentReferenceUser = firebaseFirestore.collection("Objects")
                .document(firebaseUser.getDisplayName());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_market_you, null);
        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99999999);
        //  numberPicker.setWrapSelectorWheel(false);

        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                Log.i("myTags", "onScrollStateChange, i: " + i);
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.i("myTags", "onValueChange, i1: " + i1);

            }
        });
        builder.setView(view)
                .setTitle("Buy Resources")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {

                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot documentSnapshot = transaction.get(documentReferenceInventory);
                                User user = new User();
                                user.setResource_iron(documentSnapshot.getLong("Iron").intValue());

                                Log.i("myTags", "Planet Dialog - apply: user.getIron: " + user.getResource_iron());
                                DocumentSnapshot documentSnapshot1 = transaction.get(documentReferenceUser);
                                user.setMoveToObjectName(documentSnapshot1.getString("moveToObjectName"));
                                user.setMoney(documentSnapshot1.getLong("money").intValue());
                                Log.i("myTags", "Planet Dialog - apply: current money: " + user.getMoney());
                                DocumentReference documentReferencePlanetMarket = firebaseFirestore.collection("Objects")
                                        .document(user.getMoveToObjectName());
                                DocumentSnapshot documentSnapshot2 = transaction.get(documentReferencePlanetMarket);
                                ObjectModel objectModel = new ObjectModel();
                                objectModel.setPrice_buy_iron(documentSnapshot2.getLong("price_buy_iron").intValue());
                                objectModel.setPrice_sell_iron(documentSnapshot2.getLong("price_sell_iron").intValue());
                                objectModel.setIronAmount(documentSnapshot2.getLong("iron").intValue());
                                Log.i("myTags", "Planet Dialog - apply: setPrice_sell_iron: " + objectModel.getPrice_sell_iron());

                                int selectedValue = numberPicker.getValue();
                                Log.i("myTags", "Planet Dialog - apply: selectedValue: " + selectedValue);
                                Log.i("myTags", "Planet Dialog - apply: selectedValue * price for 1: " + selectedValue * objectModel.getPrice_sell_iron());

                                if (user.getMoney() - selectedValue * objectModel.getPrice_sell_iron() > 0) {
                                    Log.i("myTags", "Planet Dialog - apply: We have ENOUGH Money ");
                                    transaction.update(documentReferenceInventory, "Iron", user.getResource_iron() + selectedValue);
                                    Log.i("myTags", "Planet Dialog - apply: new iron amount: " + user.getResource_iron() + selectedValue);
                                    transaction.update(documentReferenceUser, "money", user.getMoney() -
                                            user.getResource_iron() * objectModel.getPrice_sell_iron());
                                    Log.i("myTags", "Planet Dialog - apply: new money amount: " + (user.getMoney() -
                                            user.getResource_iron() * objectModel.getPrice_sell_iron()));

                                    transaction.update(documentReferencePlanetMarket, "iron", objectModel.getIronAmount() - selectedValue);
                                    dismiss();
                                } else {
                                    transaction.update(documentReferenceInventory, "Iron", user.getResource_iron());
                                    transaction.update(documentReferenceUser, "money", user.getMoney());
                                    transaction.update(documentReferencePlanetMarket, "price_buy_iron", objectModel.getPrice_buy_iron());
                                    Toast.makeText(getContext(), "You don't have enough money!", Toast.LENGTH_LONG).show();
                                    dismiss();
                                }
                                return null;
                            }
                        });







                        /*documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                userList.setResource_iron((documentSnapshot.getLong("iron").intValue()));
                                int selectedValue = numberPicker.getValue();
                                if (userList.getResource_iron() - selectedValue < 0) {
                                    documentReference.update("iron", 0);
                                } else {
                                    documentReference.update("iron", userList.getResource_iron() - selectedValue);
                                }
                                dismiss();
                            }
                        });*/

                    }
                });

        return builder.create();
    }
}
