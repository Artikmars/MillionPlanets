package com.artamonov.millionplanets.sectors

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import androidx.appcompat.app.AppCompatDialogFragment
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SectorsPlanetDialog : AppCompatDialogFragment() {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    private val userList: List<User>? = null
    private val objectModelList: List<SpaceObject>? = null
    private var numberPicker: NumberPicker? = null
    private var firebaseUser: FirebaseUser? = null
    private val documentReference: DocumentReference? = null
    private var documentReferenceUser: DocumentReference? = null
    private var documentReferenceInventory: DocumentReference? = null
    private val isPlanetTab = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        documentReferenceInventory = firebaseFirestore.collection("Inventory").document(firebaseUser!!.displayName!!)
        documentReferenceUser = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_market_you, null)
        numberPicker = view.findViewById(R.id.numberPicker)
        numberPicker?.minValue = 1
        numberPicker?.maxValue = 2
        //  numberPicker.setWrapSelectorWheel(false);
        numberPicker?.setOnScrollListener(
                NumberPicker.OnScrollListener { _, i -> Log.i("myTags", "onScrollStateChange, i: $i") })
        numberPicker?.setOnValueChangedListener(
                OnValueChangeListener { _, i, i1 -> Log.i("myTags", "onValueChange, i1: $i1") })
        builder.setView(view)
                .setTitle("Buy Sectors")
                .setPositiveButton(
                        "OK"
                ) { dialogInterface, i ->
                    firebaseFirestore.runTransaction<Void> { transaction ->
                        val selectedValue = numberPicker?.value
                        val documentSnapshot1 = transaction[documentReferenceUser!!]
                        val user = User()
                        user.locationName = documentSnapshot1.getString(
                                "locationName")
                        user.money = documentSnapshot1.getLong("money")
                        Log.i(
                                "myTags", "Planet Dialog - apply: current money: " +
                                user.money)
                        val documentSnapshot = transaction[documentReferenceInventory!!]
                        user.sectors = documentSnapshot
                                .getLong(user.locationName!!)
                        val documentReferencePlanet = firebaseFirestore
                                .collection("Objects")
                                .document(user.locationName!!)
                        val documentSnapshotPlanet = transaction[documentReferencePlanet]
                        val spaceObject = SpaceObject()
                        spaceObject.planetSectorsPrice = documentSnapshotPlanet
                                .getLong("sectors_price")!!
                        spaceObject.planetSectors = documentSnapshotPlanet
                                .getLong("sectors")!!
                        transaction.update(
                                documentReferenceInventory!!,
                                user.locationName!!,
                                selectedValue)
                        transaction.update(
                                documentReferenceUser!!,
                                "money", user.money!! -
                                spaceObject.planetSectorsPrice /
                                selectedValue!!)
                        transaction.update(
                                documentReferencePlanet,
                                "sectors", spaceObject.planetSectors -
                                selectedValue)
                        dismiss()
                        null
                    }
                }
        return builder.create()
    }
}