package com.artamonov.millionplanets.market

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.market.adapter.MarketYouAdapter
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_market_you.*

class MarketYouDialog : AppCompatDialogFragment() {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    private val userList: List<User>? = null
    private val spaceObjectList: List<SpaceObject>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val documentReference: DocumentReference? = null
    private var documentReferenceUser: DocumentReference? = null
    private var documentReferenceInventory: DocumentReference? = null
    private var dialogListener: MarketYouAdapter.DialogListener? = null
    private val isPlanetTab = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogListener = try {
            context as MarketYouAdapter.DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement DialogListener")
        }
    }

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
            numberPicker.setOnValueChangedListener { _, _, i1 -> Log.i("myTags", "onValueChange, i1: $i1") }
        }
        builder.setView(view)
                .setTitle("Sell Resources")
                .setPositiveButton(
                        "OK"
                ) { _, _ ->
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

                    firebaseFirestore.runTransaction<Void> { transaction ->
                        val documentSnapshot = transaction[documentReferenceInventory!!]
                        val user = User()
                        user.resource_iron = documentSnapshot
                                .getLong("Iron")
                        val documentSnapshot1 = transaction[documentReferenceUser!!]
                        user.locationName = documentSnapshot1.getString(
                                "locationName")
                        user.money = documentSnapshot1
                                .getLong("money")
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
                        if (user.resource_iron!! - selectedValue < 0) {
                            transaction.update(
                                    documentReferenceInventory!!, "Iron", 0)
                            transaction.update(
                                    documentReferenceUser!!,
                                    "money", user.money!! +
                                    user.resource_iron!!
                                    * spaceObject.price_buy_iron)
                            transaction.update(
                                    documentReferencePlanetMarket,
                                    "iron", spaceObject.ironAmount +
                                    user.resource_iron!!)
                            /* Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                                                    " Current money: " + (user.getMoney() +
                                                                                                            user.getResource_iron() * objectModel.getPrice_buy_iron()), Snackbar.LENGTH_LONG).show();*/
                            //  Toast.makeText(getActivity(), "Current
                            // money: " + (user.getMoney() +
                            //        user.getResource_iron() *
                            // objectModel.getPrice_buy_iron()),
                            // Toast.LENGTH_SHORT).show();
                            dismiss()
                        } else {
                            transaction.update(
                                    documentReferenceInventory!!,
                                    "Iron", user.resource_iron!! -
                                    selectedValue)
                            transaction.update(
                                    documentReferenceUser!!,
                                    "money", user.money!! +
                                    selectedValue
                                    * spaceObject.price_buy_iron)
                            transaction.update(
                                    documentReferencePlanetMarket,
                                    "iron", spaceObject.ironAmount +
                                    selectedValue)
                            /* Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                                                    " Current money: " + (user.getMoney() +
                                                                                                    user.getResource_iron() * objectModel.getPrice_buy_iron()), Snackbar.LENGTH_LONG).show();*/
                            // Toast.makeText(getActivity(), "Current money:
                            // " + (user.getMoney() +
                            //          user.getResource_iron() *
                            // objectModel.getPrice_buy_iron()),
                            // Toast.LENGTH_SHORT).show();
                            dismiss()
                        }
                        null
                    }
                }
        return builder.create()
    }
}