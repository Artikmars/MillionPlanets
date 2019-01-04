package com.artamonov.millionplanets.market;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.model.ObjectModel;
import com.artamonov.millionplanets.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MarketYouDialog extends AppCompatDialogFragment {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private NumberPicker numberPicker;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
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
                .setTitle("Sell Resources")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       /* documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                userList.setResource_iron((documentSnapshot.getLong("iron").intValue()));
                                userList.setMoney((documentSnapshot.getLong("money").intValue()));
                                int selectedValue = numberPicker.getValue();
                                if (userList.getResource_iron() - selectedValue < 0) {
                                    documentReference.update("iron", 0);
                                    documentReferenceMoney.update("money", userList.getMoney() + userList.getResource_iron() * );
                                } else {
                                    documentReference.update("iron", userList.getResource_iron() - selectedValue);
                                }
                                dismiss();
                            }
                        });*/

                        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {

                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot documentSnapshot = transaction.get(documentReferenceInventory);
                                User user = new User();
                                user.setResource_iron(documentSnapshot.getLong("Iron").intValue());

                                Log.i("myTags", "apply: user.getIron: " + user.getResource_iron());
                                DocumentSnapshot documentSnapshot1 = transaction.get(documentReferenceUser);
                                user.setMoveToObjectName(documentSnapshot1.getString("moveToObjectName"));
                                user.setMoney(documentSnapshot1.getLong("money").intValue());
                                Log.i("myTags", "apply: user.getName: " + user.getMoveToObjectName());
                                DocumentReference documentReferencePlanetMarket = firebaseFirestore.collection("Objects")
                                        .document(user.getMoveToObjectName());
                                DocumentSnapshot documentSnapshot2 = transaction.get(documentReferencePlanetMarket);
                                ObjectModel objectModel = new ObjectModel();
                                objectModel.setPrice_buy_iron(documentSnapshot2.getLong("price_buy_iron").intValue());
                                objectModel.setPrice_sell_iron(documentSnapshot2.getLong("price_sell_iron").intValue());
                                objectModel.setIronAmount(documentSnapshot2.getLong("iron").intValue());
                                Log.i("myTags", "apply: setPrice_buy_iron: " + objectModel.getPrice_buy_iron());
                                Log.i("myTags", "apply: setPrice_sell_iron: " + objectModel.getPrice_sell_iron());

                                int selectedValue = numberPicker.getValue();
                                if (user.getResource_iron() - selectedValue < 0) {
                                    transaction.update(documentReferenceInventory, "Iron", 0);
                                    transaction.update(documentReferenceUser, "money", user.getMoney() +
                                            user.getResource_iron() * objectModel.getPrice_buy_iron());
                                    Log.i("myTags", "apply: new iron amount on planet: " + (objectModel.getIronAmount() + user.getResource_iron()));
                                    transaction.update(documentReferencePlanetMarket, "iron", objectModel.getIronAmount() + user.getResource_iron());
                                   /* Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            " Current money: " + (user.getMoney() +
                                                    user.getResource_iron() * objectModel.getPrice_buy_iron()), Snackbar.LENGTH_LONG).show();*/
                                    //  Toast.makeText(getActivity(), "Current money: " + (user.getMoney() +
                                    //        user.getResource_iron() * objectModel.getPrice_buy_iron()), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                } else {
                                    transaction.update(documentReferenceInventory, "Iron", user.getResource_iron() - selectedValue);
                                    transaction.update(documentReferenceUser, "money", user.getMoney() +
                                            selectedValue * objectModel.getPrice_buy_iron());
                                    transaction.update(documentReferencePlanetMarket, "iron", objectModel.getIronAmount() + selectedValue);
                                   /* Snackbar.make(getActivity().findViewById(android.R.id.content),
                                           " Current money: " + (user.getMoney() +
                                           user.getResource_iron() * objectModel.getPrice_buy_iron()), Snackbar.LENGTH_LONG).show();*/
                                    // Toast.makeText(getActivity(), "Current money: " + (user.getMoney() +
                                    //          user.getResource_iron() * objectModel.getPrice_buy_iron()), Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                                return null;
                            }
                        });


                    }
                });

        return builder.create();
    }
}
