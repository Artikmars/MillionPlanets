package com.artamonov.millionplanets.shipyard

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.getCurrentShipInfo
// import com.artamonov.millionplanets.utils.getCurrentShipInfo
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.shipyard_info_activity.*
import java.util.ArrayList

class ShipyardInfoActivity : BaseActivity(R.layout.shipyard_info_activity) {
    internal var userList: User? = User()
    internal var objectModelList = SpaceObject()
    /*  User figher = new User();
    User trader = new User();
    User rs = new User();*/
    internal var shipsList: List<User> = ArrayList()

    private var documentReference: DocumentReference? = null
    private val planetDocumentReference: DocumentReference? = null
    private val rvShipyard: RecyclerView? = null
    private val shipsArrayList = ArrayList<String>()
    private var yourShip: String? = null
    private var shipToBuy: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val position = intent.getIntExtra("position", 0)
        yourShip = intent.getStringExtra("your_ship")

        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)

        shipToBuy = getCurrentShipInfo(position)
        setOnBuyButtonVisibility(shipToBuy!!)
        shipyard_hp.text = shipToBuy!!.hp.toString()
        shipyard_cargo.text = shipToBuy!!.cargoCapacity.toString()
        shipyard_shield.text = shipToBuy!!.shield.toString()
        shipyard_fuel.text = shipToBuy!!.fuel.toString()
        shipyard_jump.text = shipToBuy!!.jump.toString()
        shipyard_cost.text = shipToBuy!!.shipPrice.toString()
        shipyard_weapon_slots.text = shipToBuy!!.weaponSlots.toString()
        shipyard_scanner.text = shipToBuy!!.scanner_capacity.toString()
        shipyard_name.text = shipToBuy!!.ship
        shipyard_class.text = getCurrentShipInfo(position)?.shipClass

//        userList = User(shipToBuy!!.cargoCapacity, shipToBuy!!.fuel,shipToBuy!!.hp, shipToBuy!!.jump,
//                shipToBuy!!.scanner_capacity,shipToBuy!!.shield,
//                 shipToBuy!!.shipPrice, shipToBuy!!.ship, shipToBuy!!.shipClass, shipToBuy!!.weapon!!,
//                 shipToBuy!!.weaponSlots
//                )

        shipyard_go_back.setOnClickListener {
            finish()
        }

        shipyard_btn_buy.setOnClickListener {
            if (!ifEnoughMoney()) {
                Toast.makeText(this, "Not enough money to buy the ship!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            shipyard_btn_buy.isEnabled = false
            shipyard_btn_buy.setBackgroundColor(resources.getColor(R.color.grey))
            firebaseFirestore.runTransaction { transaction ->

                userList?.hp = shipToBuy!!.hp
                userList?.shield = shipToBuy!!.shield
                userList?.cargoCapacity = shipToBuy!!.cargoCapacity
                userList?.jump = shipToBuy!!.jump
                userList?.fuel = shipToBuy!!.fuel
                userList?.scanner_capacity = shipToBuy!!.scanner_capacity
                userList?.weaponSlots = shipToBuy!!.weaponSlots
                userList?.weapon = shipToBuy!!.weapon
                userList?.ship = shipToBuy!!.ship!!
                userList?.shipClass = shipToBuy!!.shipClass!!
                userList?.shipPrice = shipToBuy!!.shipPrice
                userList!!.money = userList!!.money!! - shipToBuy!!.shipPrice!! + userList!!.shipPrice!! / 2
                transaction.set(documentReference!!, userList!!)
                null
            }
        }
    }

    private fun showDiffStats(shipToBuy: User, userList: User) {
        if (shipToBuy.ship == userList.ship) {
            return
        }
        val hpDiff = shipToBuy.hp!! - userList.hp!!
        if (hpDiff >= 0) {
            shipyard_hp_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_hp_diff.text = "+ $hpDiff"
        } else {
            shipyard_hp_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_hp_diff.text = hpDiff.toString()
        }

        val cargoDiff = shipToBuy.cargoCapacity!! - userList.cargoCapacity!!
        if (cargoDiff >= 0) {
            shipyard_cargo_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_cargo_diff.text = "+ $cargoDiff"
        } else {
            shipyard_cargo_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_cargo_diff.text = cargoDiff.toString()
        }
        val shieldDiff = shipToBuy.shield!! - userList.shield!!
        if (shieldDiff >= 0) {
            shipyard_shield_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_shield_diff.text = "+ $shieldDiff"
        } else {
            shipyard_shield_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_shield_diff.text = shieldDiff.toString()
        }
        val fuelDiff = shipToBuy.fuel!! - userList.fuel!!
        if (fuelDiff >= 0) {
            shipyard_fuel_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_fuel_diff.text = "+ $fuelDiff"
        } else {
            shipyard_fuel_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_fuel_diff.text = fuelDiff.toString()
        }
        val jumpDiff = shipToBuy.jump!! - userList.jump!!
        if (jumpDiff >= 0) {
            shipyard_jump_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_jump_diff.text = "+ $jumpDiff"
        } else {
            shipyard_jump_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_jump_diff.text = jumpDiff.toString()
        }
        val costDiff = shipToBuy.shipPrice!! - userList.shipPrice!!
        if (costDiff >= 0) {
            shipyard_cost_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_cost_diff.text = "+ $costDiff"
        } else {
            shipyard_cost_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_cost_diff.text = costDiff.toString()
        }
        val weaponSlotsDiff = shipToBuy.weaponSlots!! - userList.weaponSlots!!
        if (weaponSlotsDiff >= 0) {
            shipyard_weapon_slots_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_weapon_slots_diff.text = "+ $weaponSlotsDiff"
        } else {
            shipyard_weapon_slots_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_weapon_slots_diff.text = weaponSlotsDiff.toString()
        }
        val scannerDiff = shipToBuy.scanner_capacity!! - userList.scanner_capacity!!
        if (scannerDiff >= 0) {
            shipyard_scanner_diff.setTextColor(resources.getColor(R.color.colorAccent))
            shipyard_scanner_diff.text = "+ $scannerDiff"
        } else {
            shipyard_scanner_diff.setTextColor(resources.getColor(R.color.red))
            shipyard_scanner_diff.text = scannerDiff.toString()
        }
    }

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, e ->
            if (doc!!.exists()) {
                userList = doc.toObject(User::class.java)
                shipyard_your_ship.text = userList!!.ship
                shipyardinfo_user_cash.text = userList!!.money.toString()
                showDiffStats(shipToBuy!!, userList!!)
            }
        }
    }

    private fun setOnBuyButtonVisibility(ship: User) {
        if (yourShip == ship.ship) {
            shipyard_btn_buy.isEnabled = false
            shipyard_btn_buy.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    private fun ifEnoughMoney(): Boolean {
        return userList!!.money!! + userList!!.shipPrice!! / 2 >= shipToBuy!!.shipPrice!!
    }
}
