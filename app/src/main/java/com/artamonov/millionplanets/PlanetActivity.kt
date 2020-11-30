package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.market.MarketActivity
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.modules.ModulesListActivity
import com.artamonov.millionplanets.scanresult.ScanResultActivity
import com.artamonov.millionplanets.sectors.SectorsActivity
import com.artamonov.millionplanets.shipyard.ShipyardActivity
import com.artamonov.millionplanets.utils.getShipFuelInfo
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.planet_activity.*

class PlanetActivity : BaseActivity(R.layout.planet_activity) {

    internal var userList = User()
    internal var objectModelList = SpaceObject()
    private var documentReference: DocumentReference? = null
    private var planetDocumentReference: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)

        planet_market.setOnClickListener {
            val intent = Intent(this, MarketActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }
        planet_get_fuel.setOnClickListener {
            val fuelToFill = getShipFuelInfo(userList.ship!!) - userList.fuel!!
            val price = fuelToFill * 1000
            if (userList.money!! >= price) {
                documentReference!!.update("fuel", getShipFuelInfo(userList.ship!!))
                documentReference!!.update("money", userList.money!! - price)
            }
        }
        planet_btn_sectors.setOnClickListener { val intent = Intent(this, SectorsActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            } }
        planet_modules.setOnClickListener {
            val intent = Intent(this, ModulesListActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }
        planet_shipyard.setOnClickListener {
            val intent = Intent(this, ShipyardActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }
        planet_take_off.setOnClickListener {
            val intent = Intent(this, ScanResultActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
            finish()
        }
        }

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList.fuel = doc.getLong("fuel")!!
                userList.money = doc.getLong("money")!!
                userList.locationName = doc.getString("locationName")
                userList.ship = doc.getString("ship")
                planet_fuel.text = userList.fuel.toString()

                planetDocumentReference = firebaseFirestore
                        .collection("Objects")
                        .document(userList.locationName!!)
                planetDocumentReference!!
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                objectModelList = documentSnapshot.toObject(SpaceObject::class.java)!!
                                planet_class.text = objectModelList.planetClass
                                planet_sectors.text = objectModelList
                                        .availableSectors.toString()
                                shipyard_hp.text = objectModelList.planetSize
                                planet_money.text = userList.money.toString()

                                setObjectsAccessLevel()
                            }
                        }
            }
        }
    }

    private fun setObjectsAccessLevel() {

        val occupationLevel = (6 - objectModelList
                .availableSectors).toDouble() / 6

        if (occupationLevel < 0.25) {
            Log.i(
                    "myTags",
                    "objectModelList.getPlanetSectors: < 0.25 - $occupationLevel")
            planet_get_fuel.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
            planet_get_fuel.isEnabled = false
            planet_market.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
            planet_market.isEnabled = false
            planet_shipyard.isEnabled = false
            planet_shipyard.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
        }
        if (occupationLevel >= 0.25 && occupationLevel < 0.5) {
            Log.i(
                    "myTags",
                    "objectModelList.getPlanetSectors: 0.25-0.5 - $occupationLevel")
            planet_market.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
            planet_market.isEnabled = false
            planet_get_fuel.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            planet_get_fuel.isEnabled = true
            planet_shipyard.isEnabled = false
            planet_shipyard.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
        }
        if (occupationLevel >= 0.5 && occupationLevel < 0.75) {
            Log.i(
                    "myTags",
                    "objectModelList.getPlanetSectors:  0.5 more - $occupationLevel")
            planet_market.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            planet_market.isEnabled = true
            planet_get_fuel.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            planet_get_fuel.isEnabled = true
            planet_shipyard.isEnabled = false
            planet_shipyard.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
        }
        if (occupationLevel >= 0.75) {
            Log.i(
                    "myTags",
                    "objectModelList.getPlanetSectors:  0.5 more - $occupationLevel")
            planet_market.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            planet_market.isEnabled = true
            planet_get_fuel.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            planet_get_fuel.isEnabled = true
            planet_shipyard.isEnabled = true
            planet_shipyard.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
        }

        /*  if (objectModelList.getPlanetSectors() / 6 < 0.75) {
                        Log.i("myTags", "objectModelList.getPlanetSectors: " + objectModelList.getPlanetSectors());
                    }*/
    }
}
