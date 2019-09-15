package com.artamonov.millionplanets.sectors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
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

public class SectorsYouDialog extends AppCompatDialogFragment {
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<User> userList;
    private List<ObjectModel> objectModelList;
    private NumberPicker numberPicker;
    private FirebaseUser firebaseUser;
    private DocumentReference documentReference;
    private DocumentReference documentReferenceUser;
    private DocumentReference documentReferenceInventory;
    private boolean isPlanetTab;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        documentReferenceInventory =
                firebaseFirestore.collection("Inventory").document(firebaseUser.getDisplayName());
        documentReferenceUser =
                firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_market_you, null);
        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(2);
        //  numberPicker.setWrapSelectorWheel(false);

        numberPicker.setOnScrollListener(
                new NumberPicker.OnScrollListener() {
                    @Override
                    public void onScrollStateChange(NumberPicker numberPicker, int i) {
                        Log.i("myTags", "onScrollStateChange, i: " + i);
                    }
                });

        numberPicker.setOnValueChangedListener(
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        Log.i("myTags", "onValueChange, i1: " + i1);
                    }
                });
        builder.setView(view)
                .setTitle("Sell Sectors")
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseFirestore.runTransaction(
                                        new Transaction.Function<Void>() {

                                            @Override
                                            public Void apply(Transaction transaction)
                                                    throws FirebaseFirestoreException {
                                                int selectedValue = numberPicker.getValue();
                                                DocumentSnapshot documentSnapshot1 =
                                                        transaction.get(documentReferenceUser);
                                                User user = new User();
                                                user.setMoveToObjectName(
                                                        documentSnapshot1.getString(
                                                                "moveToObjectName"));
                                                user.setMoney(
                                                        documentSnapshot1
                                                                .getLong("money")
                                                                .intValue());
                                                Log.i(
                                                        "myTags",
                                                        "Planet Dialog - apply: current money: "
                                                                + user.getMoney());

                                                DocumentSnapshot documentSnapshot =
                                                        transaction.get(documentReferenceInventory);
                                                user.setSectors(
                                                        documentSnapshot
                                                                .getLong(user.getMoveToObjectName())
                                                                .intValue());

                                                DocumentReference documentReferencePlanet =
                                                        firebaseFirestore
                                                                .collection("Objects")
                                                                .document(
                                                                        user.getMoveToObjectName());
                                                DocumentSnapshot documentSnapshotPlanet =
                                                        transaction.get(documentReferencePlanet);
                                                ObjectModel objectModel = new ObjectModel();
                                                objectModel.setPlanetSectorsPrice(
                                                        documentSnapshotPlanet
                                                                .getLong("sectors_price")
                                                                .intValue());
                                                objectModel.setPlanetSectors(
                                                        documentSnapshotPlanet
                                                                .getLong("sectors")
                                                                .intValue());
                                                if (selectedValue == 1) {
                                                    transaction.update(
                                                            documentReferenceInventory,
                                                            user.getMoveToObjectName(),
                                                            1);
                                                } else {
                                                    transaction.update(
                                                            documentReferenceInventory,
                                                            user.getMoveToObjectName(),
                                                            0);
                                                }
                                                transaction.update(
                                                        documentReferenceUser,
                                                        "money",
                                                        user.getMoney()
                                                                + objectModel
                                                                                .getPlanetSectorsPrice()
                                                                        / selectedValue);
                                                transaction.update(
                                                        documentReferencePlanet,
                                                        "sectors",
                                                        objectModel.getPlanetSectors()
                                                                + selectedValue);
                                                dismiss();
                                                return null;
                                            }
                                        });
                            }
                        });
        return builder.create();
    }
}
