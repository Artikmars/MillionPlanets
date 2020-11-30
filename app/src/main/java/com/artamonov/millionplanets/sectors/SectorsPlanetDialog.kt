package com.artamonov.millionplanets.sectors

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_market_you.*

class SectorsPlanetDialog : AppCompatDialogFragment() {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseUser: FirebaseUser? = null
    private var documentReferenceUser: DocumentReference? = null
    private var documentReferenceInventory: DocumentReference? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        documentReferenceInventory = firebaseFirestore.collection("Inventory").document(firebaseUser!!.displayName!!)
        documentReferenceUser = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_market_you, null)
        numberPicker.apply {
            minValue = 1
            maxValue = 2
            setOnScrollListener { _, i -> Log.i("myTags", "onScrollStateChange, i: $i") }
            setOnValueChangedListener { _, _, i1 -> Log.i("myTags", "onValueChange, i1: $i1") }
        }
        builder.setView(view)
                .setTitle("Buy Sectors")
                .setPositiveButton(
                        "OK"
                ) { _, i ->
                    firebaseFirestore.runTransaction<Void> { transaction ->
                        val selectedValue = numberPicker?.value
                        val documentSnapshot1 = transaction[documentReferenceUser!!]
                        val user = User()
                        user.locationName = documentSnapshot1.getString(
                                "locationName")
                        user.money = documentSnapshot1.getLong("money")
                        val documentSnapshot = transaction[documentReferenceInventory!!]
//                        user.sectors = documentSnapshot
//                                .getLong(user.locationName!!)
                        val documentReferencePlanet = firebaseFirestore
                                .collection("Objects")
                                .document(user.locationName!!)
                        val documentSnapshotPlanet = transaction[documentReferencePlanet]
                        val spaceObject = SpaceObject()
                        spaceObject.planetSectorsPrice = documentSnapshotPlanet
                                .getLong("sectors_price")!!
                        spaceObject.availableSectors = documentSnapshotPlanet
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
                                "sectors", spaceObject.availableSectors -
                                selectedValue)
                        dismiss()
                        null
                    }
                }
        return builder.create()
    }
}