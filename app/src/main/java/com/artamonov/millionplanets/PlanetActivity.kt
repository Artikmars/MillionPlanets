package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.market.MarketActivity
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.modules.ModulesListActivity
import com.artamonov.millionplanets.sectors.SectorsActivity
import com.artamonov.millionplanets.shipyard.ShipyardActivity
import com.artamonov.millionplanets.utils.Utils
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.planet.*

class PlanetActivity : BaseActivity() {

    internal var userList = User()
    internal var objectModelList = ObjectModel()
    private var documentReference: DocumentReference? = null
    private var planetDocumentReference: DocumentReference? = null

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, e ->
            if (doc!!.exists()) {
                userList.fuel = doc.getLong("fuel")!!.toInt()
                userList.money = doc.getLong("money")!!.toInt()
                userList.moveToObjectName = doc.getString("moveToObjectName")
                userList.ship = doc.getString("ship")
                planet_fuel.text = userList.fuel.toString()

                planetDocumentReference = firebaseFirestore
                        .collection("Objects")
                        .document(userList.moveToObjectName!!)
                planetDocumentReference!!
                        .get()
                        .addOnSuccessListener(
                                object : OnSuccessListener<DocumentSnapshot> {
                                    override fun onSuccess(
                                        documentSnapshot: DocumentSnapshot
                                    ) {
                                        objectModelList.planetClass = documentSnapshot.getString("class")
                                        objectModelList.planetSize = documentSnapshot.getString("size")
                                        objectModelList.planetSectors = documentSnapshot
                                                .getLong("sectors")!!
                                                .toInt()
                                        planet_class.text = objectModelList.planetClass
                                        planet_sectors.text = objectModelList
                                                        .planetSectors.toString()
                                        shipyard_hp.text = objectModelList.planetSize
                                        planet_money.text = userList.money.toString()

                                        setObjectsAccessLevel()
                                    }

                                    private fun setObjectsAccessLevel() {

                                        val occupationLevel = (6 - objectModelList
                                                .planetSectors).toDouble() / 6

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
                                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.planet)
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)
    }

    fun onGetFuel(view: View) {
        val fuelToFill = Utils.getShipFuelInfo(userList.ship!!) - userList.fuel
        val price = fuelToFill * 1000
        if (userList.money >= price) {
            documentReference!!.update("fuel", Utils.getShipFuelInfo(userList.ship!!))
            documentReference!!.update("money", userList.money - price)
        }
    }

    fun onTakeOff(view: View) {
        startActivity(Intent(this, MainOptionsActivity::class.java))
    }

    fun onGoToMarket(view: View) {
        val intent = Intent(this, MarketActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    fun onGoToSectors(view: View) {
        val intent = Intent(this, SectorsActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    fun onGoToShipyard(view: View) {
        val intent = Intent(this, ShipyardActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    fun onGoToModules(view: View) {
        val intent = Intent(this, ModulesListActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }
}