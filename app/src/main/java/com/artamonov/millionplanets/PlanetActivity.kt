package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityPlanetBinding
import com.artamonov.millionplanets.market.MarketActivity
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.modules.ModulesListActivity
import com.artamonov.millionplanets.scanresult.ScanResultActivity
import com.artamonov.millionplanets.sectors.SectorsActivity
import com.artamonov.millionplanets.shipyard.ShipyardActivity
import com.artamonov.millionplanets.utils.getShipFuelInfo
import com.google.firebase.firestore.DocumentReference

class PlanetActivity : BaseActivity() {

    internal var userList = User()
    private var objectModelList = SpaceObject()
    private var documentReference: DocumentReference? = null
    private var planetDocumentReference: DocumentReference? = null

    lateinit var binding: ActivityPlanetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)

        binding.planetMarket.setOnClickListener {
            val intent = Intent(this, MarketActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }
        binding.planetGetFuel.setOnClickListener {
            val fuelToFill = getShipFuelInfo(userList.ship!!) - userList.fuel!!
            val price = fuelToFill * 1000
            if (userList.money!! >= price) {
                documentReference!!.update("fuel", getShipFuelInfo(userList.ship!!))
                documentReference!!.update("money", userList.money!! - price)
            }
        }
        binding.planetBtnSectors.setOnClickListener { val intent = Intent(this, SectorsActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            } }
        binding.planetModules.setOnClickListener {
            val intent = Intent(this, ModulesListActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }
        binding.planetShipyard.setOnClickListener {
            val intent = Intent(this, ShipyardActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }
        binding.planetTakeOff.setOnClickListener {
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
                binding.planetFuel.text = userList.fuel.toString()

                planetDocumentReference = firebaseFirestore
                        .collection("Objects")
                        .document(userList.locationName!!)
                planetDocumentReference!!
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                objectModelList = documentSnapshot.toObject(SpaceObject::class.java)!!
                                binding.planetClass.text = objectModelList.planetClass
                                binding.planetSectors.text = objectModelList
                                        .availableSectors.toString()
                                binding.shipyardHp.text = objectModelList.planetSize
                                binding.planetMoney.text = userList.money.toString()

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
            binding.planetGetFuel.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
            binding.planetGetFuel.isEnabled = false
            binding.planetMarket.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
            binding.planetMarket.isEnabled = false
            binding.planetShipyard.isEnabled = false
            binding.planetShipyard.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
        }
        if (occupationLevel >= 0.25 && occupationLevel < 0.5) {
            Log.i(
                    "myTags",
                    "objectModelList.getPlanetSectors: 0.25-0.5 - $occupationLevel")
            binding.planetMarket.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
            binding.planetMarket.isEnabled = false
            binding.planetGetFuel.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            binding.planetGetFuel.isEnabled = true
            binding.planetShipyard.isEnabled = false
            binding.planetShipyard.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
        }
        if (occupationLevel >= 0.5 && occupationLevel < 0.75) {
            Log.i(
                    "myTags",
                    "objectModelList.getPlanetSectors:  0.5 more - $occupationLevel")
            binding.planetMarket.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            binding.planetMarket.isEnabled = true
            binding.planetGetFuel.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            binding.planetGetFuel.isEnabled = true
            binding.planetShipyard.isEnabled = false
            binding.planetShipyard.setBackgroundColor(
                    resources
                            .getColor(R.color.grey))
        }
        if (occupationLevel >= 0.75) {
            Log.i(
                    "myTags",
                    "objectModelList.getPlanetSectors:  0.5 more - $occupationLevel")
            binding.planetMarket.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            binding.planetMarket.isEnabled = true
            binding.planetGetFuel.setBackgroundColor(
                    resources
                            .getColor(
                                    R.color
                                            .colorAccent))
            binding.planetGetFuel.isEnabled = true
            binding.planetShipyard.isEnabled = true
            binding.planetShipyard.setBackgroundColor(
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
