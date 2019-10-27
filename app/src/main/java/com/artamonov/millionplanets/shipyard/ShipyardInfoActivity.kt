package com.artamonov.millionplanets.shipyard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.main_options.*
import kotlinx.android.synthetic.main.shipyard_info.*
import java.util.ArrayList

class ShipyardInfoActivity : AppCompatActivity() {
    internal var firebaseFirestore = FirebaseFirestore.getInstance()
    internal var userList: User? = User()
    internal var objectModelList = ObjectModel()
    /*  User figher = new User();
    User trader = new User();
    User rs = new User();*/
    internal var shipsList: List<User> = ArrayList()

    private var documentReference: DocumentReference? = null
    private val planetDocumentReference: DocumentReference? = null
    private var firebaseUser: FirebaseUser? = null
    private var tv_YourShip: TextView? = null
    private var tvUserCash: TextView? = null
    private var tvUserBank: TextView? = null
    private var btnBuy: Button? = null
    private val rvShipyard: RecyclerView? = null
    private val shipsArrayList = ArrayList<String>()
    private var yourShip: String? = null
    private var shipToBuy: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shipyard_info)
        val intent = intent
        val position = intent.getIntExtra("position", 0)
        yourShip = intent.getStringExtra("your_ship")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)

        val tvHp = findViewById<TextView>(R.id.shipyard_hp)
        val tvCargo = findViewById<TextView>(R.id.shipyard_cargo)
        val tvShield = findViewById<TextView>(R.id.shipyard_shield)
        val tvFuel = findViewById<TextView>(R.id.shipyard_fuel)
        val tvJump = findViewById<TextView>(R.id.shipyard_jump)
        val tvCost = findViewById<TextView>(R.id.shipyard_cost)
        val tvWeaponSlots = findViewById<TextView>(R.id.shipyard_weapon_slots)
        val tv_ScannerCapacity = findViewById<TextView>(R.id.shipyard_scanner)
        tv_YourShip = findViewById(R.id.shipyard_your_ship)
        val tvShipyardName = findViewById<TextView>(R.id.shipyard_name)
        val tvClass = findViewById<TextView>(R.id.shipyard_class)
        btnBuy = findViewById(R.id.shipyard_btn_buy)
        tvUserCash = findViewById(R.id.shipyardinfo_user_cash)
        tvUserBank = findViewById(R.id.shipyardinfo_user_bank)

        shipToBuy = Utils.getCurrentShipInfo(position)
        setOnBuyButtonVisibility(shipToBuy!!)
        tvHp.text = shipToBuy!!.hp.toString()
        tvCargo.text = shipToBuy!!.cargo.toString()
        tvShield.text = shipToBuy!!.shield.toString()
        tvFuel.text = shipToBuy!!.fuel.toString()
        tvJump.text = shipToBuy!!.jump.toString()
        tvCost.text = shipToBuy!!.shipPrice.toString()
        tvWeaponSlots.text = shipToBuy!!.weaponSlots.toString()
        tv_ScannerCapacity.text = shipToBuy!!.scanner_capacity.toString()
        tvShipyardName.text = shipToBuy!!.ship
        tvClass.text = Utils.getCurrentShipInfo(position)?.shipClass

//        userList = User(shipToBuy!!.cargo, shipToBuy!!.fuel,shipToBuy!!.hp, shipToBuy!!.jump,
//                shipToBuy!!.scanner_capacity,shipToBuy!!.shield,
//                 shipToBuy!!.shipPrice, shipToBuy!!.ship, shipToBuy!!.shipClass, shipToBuy!!.weapon!!,
//                 shipToBuy!!.weaponSlots
//                )

        shipyard_btn_buy.setOnClickListener {
            if (!ifEnoughMoney()) {
                Toast.makeText(this, "Not enough money to buy the ship!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            btnBuy!!.isEnabled = false
            btnBuy!!.setBackgroundColor(resources.getColor(R.color.grey))
            firebaseFirestore.runTransaction { transaction ->

                userList?.hp = shipToBuy!!.hp
                userList?.shield = shipToBuy!!.shield
                userList?.cargo = shipToBuy!!.cargo
                userList?.jump = shipToBuy!!.jump
                userList?.fuel = shipToBuy!!.fuel
                userList?.scanner_capacity = shipToBuy!!.scanner_capacity
                userList?.weaponSlots = shipToBuy!!.weaponSlots
                userList?.ship = shipToBuy!!.ship!!
                userList?.shipClass = shipToBuy!!.shipClass!!
                userList?.shipPrice = shipToBuy!!.shipPrice
                userList!!.money = userList!!.money - shipToBuy!!.shipPrice + userList!!.shipPrice / 2
                transaction.set(documentReference!!, userList!!)
                null
            }
        }
    }

    private fun showDiffStats(shipToBuy: User, userList: User) {
        if (shipToBuy.ship == userList.ship) {
            return
        }
        val tvHpDiff = findViewById<TextView>(R.id.shipyard_hp_diff)
        val tvCargoDiff = findViewById<TextView>(R.id.shipyard_cargo_diff)
        val tvShieldDiff = findViewById<TextView>(R.id.shipyard_shield_diff)
        val tvFuelDiff = findViewById<TextView>(R.id.shipyard_fuel_diff)
        val tvJumpDiff = findViewById<TextView>(R.id.shipyard_jump_diff)
        val tvCostDiff = findViewById<TextView>(R.id.shipyard_cost_diff)
        val tvWeaponSlotsDiff = findViewById<TextView>(R.id.shipyard_weapon_slots_diff)
        val tvScannerCapacityDiff = findViewById<TextView>(R.id.shipyard_scanner_diff)
        val hpDiff = shipToBuy.hp - userList.hp
        if (hpDiff >= 0) {
            tvHpDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvHpDiff.text = "+ $hpDiff"
        } else {
            tvHpDiff.setTextColor(resources.getColor(R.color.red))
            tvHpDiff.text = hpDiff.toString()
        }

        val cargoDiff = shipToBuy.cargo - userList.cargo
        if (cargoDiff >= 0) {
            tvCargoDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvCargoDiff.text = "+ $cargoDiff"
        } else {
            tvCargoDiff.setTextColor(resources.getColor(R.color.red))
            tvCargoDiff.text = cargoDiff.toString()
        }
        val shieldDiff = shipToBuy.shield - userList.shield
        if (shieldDiff >= 0) {
            tvShieldDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvShieldDiff.text = "+ $shieldDiff"
        } else {
            tvShieldDiff.setTextColor(resources.getColor(R.color.red))
            tvShieldDiff.text = shieldDiff.toString()
        }
        val fuelDiff = shipToBuy.fuel - userList.fuel
        if (fuelDiff >= 0) {
            tvFuelDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvFuelDiff.text = "+ $fuelDiff"
        } else {
            tvFuelDiff.setTextColor(resources.getColor(R.color.red))
            tvFuelDiff.text = fuelDiff.toString()
        }
        val jumpDiff = shipToBuy.jump - userList.jump
        if (jumpDiff >= 0) {
            tvJumpDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvJumpDiff.text = "+ $jumpDiff"
        } else {
            tvJumpDiff.setTextColor(resources.getColor(R.color.red))
            tvJumpDiff.text = jumpDiff.toString()
        }
        val costDiff = shipToBuy.shipPrice - userList.shipPrice
        if (costDiff >= 0) {
            tvCostDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvCostDiff.text = "+ $costDiff"
        } else {
            tvCostDiff.setTextColor(resources.getColor(R.color.red))
            tvCostDiff.text = costDiff.toString()
        }
        val weaponSlotsDiff = shipToBuy.weaponSlots - userList.weaponSlots
        if (weaponSlotsDiff >= 0) {
            tvWeaponSlotsDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvWeaponSlotsDiff.text = "+ $weaponSlotsDiff"
        } else {
            tvWeaponSlotsDiff.setTextColor(resources.getColor(R.color.red))
            tvWeaponSlotsDiff.text = weaponSlotsDiff.toString()
        }
        val scannerDiff = shipToBuy.scanner_capacity - userList.scanner_capacity
        if (scannerDiff >= 0) {
            tvScannerCapacityDiff.setTextColor(resources.getColor(R.color.colorAccent))
            tvScannerCapacityDiff.text = "+ $scannerDiff"
        } else {
            tvScannerCapacityDiff.setTextColor(resources.getColor(R.color.red))
            tvScannerCapacityDiff.text = scannerDiff.toString()
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
                userList
                tv_YourShip!!.text = userList!!.ship
                tvUserCash!!.text = userList!!.money.toString()
                showDiffStats(shipToBuy!!, userList!!)
            }
        }
    }

    private fun setOnBuyButtonVisibility(ship: User) {
        if (yourShip == ship.ship) {
            btnBuy!!.isEnabled = false
            btnBuy!!.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    private fun ifEnoughMoney(): Boolean {
        return userList!!.money + userList!!.shipPrice / 2 >= shipToBuy!!.shipPrice
    }
}
