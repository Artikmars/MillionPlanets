package com.artamonov.millionplanets.shipyard

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityShipyardInfoBinding
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.getCurrentShipInfo

class ShipyardInfoActivity : BaseActivity() {
    internal var userList: User? = User()
    private var yourShip: String? = null
    private var shipToBuy: User? = null
    lateinit var binding: ActivityShipyardInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShipyardInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val position = intent.getIntExtra("position", 0)
        yourShip = intent.getStringExtra("your_ship")

        shipToBuy = getCurrentShipInfo(position)
        setOnBuyButtonVisibility(shipToBuy!!)
        binding.shipyardHp.text = shipToBuy!!.hp.toString()
        binding.shipyardCargo.text = shipToBuy!!.cargoCapacity.toString()
        binding.shipyardShield.text = shipToBuy!!.shield.toString()
        binding.shipyardFuel.text = shipToBuy!!.fuel.toString()
        binding.shipyardJump.text = shipToBuy!!.jump.toString()
        binding.shipyardCost.text = shipToBuy!!.shipPrice.toString()
        binding.shipyardWeaponSlots.text = shipToBuy!!.weaponSlots.toString()
        binding.shipyardScanner.text = shipToBuy!!.scanner_capacity.toString()
        binding.shipyardName.text = shipToBuy!!.ship
        binding.shipyardClass.text = getCurrentShipInfo(position)?.shipClass

//        userList = User(shipToBuy!!.cargoCapacity, shipToBuy!!.fuel,shipToBuy!!.hp, shipToBuy!!.jump,
//                shipToBuy!!.scanner_capacity,shipToBuy!!.shield,
//                 shipToBuy!!.shipPrice, shipToBuy!!.ship, shipToBuy!!.shipClass, shipToBuy!!.weapon!!,
//                 shipToBuy!!.weaponSlots
//                )

        binding.shipyardGoBack.setOnClickListener {
            finish()
        }

        binding.shipyardBtnBuy.setOnClickListener {
            if (!ifEnoughMoney()) {
                Toast.makeText(this, "Not enough money to buy the ship!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.shipyardBtnBuy.isEnabled = false
            binding.shipyardBtnBuy.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
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
                transaction.set(userDocument, userList!!)
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
            binding.shipyardHpDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardHpDiff.text = "+ $hpDiff"
        } else {
            binding.shipyardHpDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardHpDiff.text = hpDiff.toString()
        }

        val cargoDiff = shipToBuy.cargoCapacity!! - userList.cargoCapacity!!
        if (cargoDiff >= 0) {
            binding.shipyardCargoDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardCargoDiff.text = "+ $cargoDiff"
        } else {
            binding.shipyardCargoDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardCargoDiff.text = cargoDiff.toString()
        }
        val shieldDiff = shipToBuy.shield!! - userList.shield!!
        if (shieldDiff >= 0) {
            binding.shipyardShieldDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardShieldDiff.text = "+ $shieldDiff"
        } else {
            binding.shipyardShieldDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardShieldDiff.text = shieldDiff.toString()
        }
        val fuelDiff = shipToBuy.fuel!! - userList.fuel!!
        if (fuelDiff >= 0) {
            binding.shipyardFuelDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardFuelDiff.text = "+ $fuelDiff"
        } else {
            binding.shipyardFuelDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardFuelDiff.text = fuelDiff.toString()
        }
        val jumpDiff = shipToBuy.jump!! - userList.jump!!
        if (jumpDiff >= 0) {
            binding.shipyardJumpDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardJumpDiff.text = "+ $jumpDiff"
        } else {
            binding.shipyardJumpDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardJumpDiff.text = jumpDiff.toString()
        }
        val costDiff = shipToBuy.shipPrice!! - userList.shipPrice!!
        if (costDiff >= 0) {
            binding.shipyardCostDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardCostDiff.text = "+ $costDiff"
        } else {
            binding.shipyardCostDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardCostDiff.text = costDiff.toString()
        }
        val weaponSlotsDiff = shipToBuy.weaponSlots!! - userList.weaponSlots!!
        if (weaponSlotsDiff >= 0) {
            binding.shipyardWeaponSlotsDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardWeaponSlotsDiff.text = "+ $weaponSlotsDiff"
        } else {
            binding.shipyardWeaponSlotsDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardWeaponSlotsDiff.text = weaponSlotsDiff.toString()
        }
        val scannerDiff = shipToBuy.scanner_capacity!! - userList.scanner_capacity!!
        if (scannerDiff >= 0) {
            binding.shipyardScannerDiff.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            binding.shipyardScannerDiff.text = "+ $scannerDiff"
        } else {
            binding.shipyardScannerDiff.setTextColor(ContextCompat.getColor(this, R.color.red))
            binding.shipyardScannerDiff.text = scannerDiff.toString()
        }
    }

    override fun onStart() {
        super.onStart()
        userDocument.addSnapshotListener(
                this
        ) { doc, e ->
            if (doc!!.exists()) {
                userList = doc.toObject(User::class.java)
                binding.shipyardYourShip.text = userList!!.ship
                binding.shipyardinfoUserCash.text = userList!!.money.toString()
                showDiffStats(shipToBuy!!, userList!!)
            }
        }
    }

    private fun setOnBuyButtonVisibility(ship: User) {
        if (yourShip == ship.ship) {
            binding.shipyardBtnBuy.isEnabled = false
            binding.shipyardBtnBuy.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
        }
    }

    private fun ifEnoughMoney(): Boolean {
        return userList!!.money!! + userList!!.shipPrice!! / 2 >= shipToBuy!!.shipPrice!!
    }
}
