package com.artamonov.millionplanets.market

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_market_you.*

@AndroidEntryPoint
class MarketPlanetDialog : AppCompatDialogFragment() {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var userList = User()
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var documentReferenceUser: DocumentReference? = null
    private var documentReferenceInventory: DocumentReference? = null
    private val isPlanetTab = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        documentReferenceInventory = firebaseFirestore.collection("Inventory").document(firebaseUser!!.displayName!!)
        documentReferenceUser = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_market_you, null)
        numberPicker?.apply {
            minValue = 1
            maxValue = 99999999
            setOnScrollListener { _, i -> Log.i("myTags", "onScrollStateChange, i: $i") }
            setOnValueChangedListener { _, _, i1 -> Log.i("myTags", "onValueChange, i1: $i1") }
        }
//        numberPicker?.minValue = 1
//        numberPicker?.maxValue = 99999999
        //  numberPicker.setWrapSelectorWheel(false);
        builder.setView(view)
                .setTitle("Buy Resources")
                .setPositiveButton(
                        "OK"
                ) { _, _ ->
                    firebaseFirestore.runTransaction<Void>(
                            Transaction.Function { transaction ->
                                val documentSnapshot = transaction[documentReferenceInventory!!]
                                val user = User()
                                user.resource_iron = documentSnapshot
                                        .getLong("Iron")
                                val documentSnapshot1 = transaction[documentReferenceUser!!]
                                user.locationName = documentSnapshot1.getString(
                                        "locationName")
                                user.money = documentSnapshot1
                                        .getLong("money")
                                user.cargoCapacity = documentSnapshot1
                                        .getLong("cargoCapacity")
                                val documentReferencePlanetMarket = firebaseFirestore
                                        .collection("Objects")
                                        .document(user.locationName!!)
                                val documentSnapshot2 = transaction[documentReferencePlanetMarket]
                                val spaceObject = SpaceObject()
                                spaceObject.price_buy_iron = documentSnapshot2
                                        .getLong("price_buy_iron")!!
                                spaceObject.price_sell_iron = documentSnapshot2
                                        .getLong("price_sell_iron")!!
                                spaceObject.ironAmount = documentSnapshot2
                                        .getLong("iron")!!
                                val selectedValue = numberPicker.value
                                if (user.resource_iron!! + selectedValue
                                        > user.cargoCapacity!!) {
                                    Toast.makeText(
                                            activity,
                                            "Your cargoCapacity is full!",
                                            Toast.LENGTH_SHORT)
                                            .show()
                                    transaction.update(
                                            documentReferenceInventory!!,
                                            "Iron",
                                            user.resource_iron)
                                    transaction.update(
                                            documentReferenceUser!!,
                                            "money",
                                            user.money)
                                    transaction.update(
                                            documentReferencePlanetMarket,
                                            "iron",
                                            spaceObject.ironAmount)
                                    return@Function null
                                }
                                if (user.money!! -
                                        selectedValue
                                        * spaceObject.price_sell_iron
                                        > 0) {
                                    transaction.update(
                                            documentReferenceInventory!!,
                                            "Iron", user.resource_iron!! +
                                            selectedValue)
                                    transaction.update(
                                            documentReferenceUser!!,
                                            "money", user.money!! -
                                            selectedValue
                                            * spaceObject.price_sell_iron)
                                    transaction.update(
                                            documentReferencePlanetMarket,
                                            "iron", spaceObject.ironAmount -
                                            selectedValue)
                                    dismiss()
                                } else {
                                    val resourceAmountToSell = (user.money!! /
                                            spaceObject.price_sell_iron)
                                    transaction.update(
                                            documentReferenceInventory!!,
                                            "Iron", user.resource_iron!! +
                                            resourceAmountToSell)
                                    transaction.update(
                                            documentReferenceUser!!,
                                            "money", user.money!! -
                                            resourceAmountToSell
                                            * spaceObject.price_sell_iron)
                                    transaction.update(
                                            documentReferencePlanetMarket,
                                            "iron", spaceObject.ironAmount -
                                            resourceAmountToSell)
                                    //  Toast.makeText(getActivity(), "You don't
                                    // have enough money!",
                                    // Toast.LENGTH_LONG).show();
                                    dismiss()
                                }
                                null
                            })

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
        return builder.create()
    }
}