package com.artamonov.millionplanets.modules

import android.os.Bundle
import android.view.View
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.Module
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.artamonov.millionplanets.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.modules_info.*
import java.util.ArrayList

class ModulesInfoActivity : BaseActivity() {
    internal var userList = User()
    internal var armingList = User()
    internal var objectModelList = ObjectModel()
    internal var moduleList: List<Module> = ArrayList()

    private var userDocumentReference: DocumentReference? = null
    private var armingDocumentReference: DocumentReference? = null
    private var modulesDocumentReference: DocumentReference? = null
    private val planetDocumentReference: DocumentReference? = null
    private var module: Module? = null
    private var position: Int = 0
    private var currentModule: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modules_info)
        val intent = intent
        position = intent.getIntExtra("position", -1)

        userDocumentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        armingDocumentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
                .collection("Inventory").document("arming")

        module = Utils.getCurrentModuleInfo(java.lang.Long.valueOf(position.toLong()))
        modulesinfo_class.text = module!!.moduleClass
        modulesinfo_cost.text = module!!.price.toString()
        modulesinfo_damageHp.text = module!!.damageHP.toString()
        modulesinfo_cost.text = module!!.price.toString()

        modulesinfo_buy.setOnClickListener {
            val batch = firebaseFirestore.batch()

            if (isSlotAvailable()) {
                val weapon = userList.weapon!!.toMutableList()
                weapon.add(Weapon(position.toLong(), true))

//                val weaponType = userList.weaponType!!.toMutableList()
//                weaponType.add(weaponType.size, Utils.getCurrentModuleInfo(position.toLong())!!.type)
                batch.update(userDocumentReference!!, "weapon", weapon)
            } else {
                    if (armingList.weapon != null) {
                        val weapon = armingList.weapon!!.toMutableList()
                        weapon.add(Weapon(position.toLong(), true))
                        batch.update(armingDocumentReference!!, "weapon", weapon)
                    } else {
                        val weapon = mutableListOf<Weapon>()
                        weapon.add(Weapon(position.toLong(), true))
                        armingList.weapon = weapon
                        batch.set(armingDocumentReference!!, weapon)
                    }
            }

//            val moduleMap = HashMap<String, Any>()
//            moduleMap["damageHP"] = module!!.damageHP
//            moduleMap["weaponClass"] = module!!.moduleClass
//            moduleMap["weaponName"] = module!!.name
            batch.update(userDocumentReference!!, "money", userList.money - module!!.price)
            batch.commit()
                    .addOnCompleteListener {
//                        modulesinfo_buy.isEnabled = false
//                        modulesinfo_buy.setBackgroundColor(resources.getColor(R.color.grey))
                        modulesinfo_sell.isEnabled = true
                        modulesinfo_sell.setBackgroundColor(
                                resources.getColor(R.color.colorAccent))
                        Snackbar.make(
                                modulesinfo_parentLayout,
                                "The item was successfully bought",
                                Snackbar.LENGTH_SHORT)
                                .show()
                    }
        }

        modulesinfo_sell.setOnClickListener {
            val batch = firebaseFirestore.batch()

            when {
                isInInventory() -> {
                    val weapon = armingList.weapon!!.toMutableList()
                    weapon.removeAt(weapon.indexOf(Weapon(position.toLong())))
                    batch.update(armingDocumentReference!!, "weapon", weapon)
                }
                isInstalled() -> {
                    val weapon = userList.weapon!!.toMutableList()
                    weapon.removeAt(weapon.indexOf(Weapon(position.toLong())))
                    batch.update(userDocumentReference!!, "weapon", weapon)
                }
                else -> Snackbar.make(
                        modulesinfo_parentLayout,
                        "The item has not been found",
                        Snackbar.LENGTH_SHORT)
                        .show()
            }

//            val moduleMap = HashMap<String, Any>()
//            moduleMap["damageHP"] = Utils.getCurrentModuleInfo(0L)!!.damageHP
//            moduleMap["weaponClass"] = Utils.getCurrentModuleInfo(0L)!!.moduleClass
//            moduleMap["weaponName"] = Utils.getCurrentModuleInfo(0L)!!.name
            batch.update(userDocumentReference!!, "money", userList.money + module!!.price / 2)
            batch.commit()
                    .addOnCompleteListener {
                        if (isEnoughMoneyToBuy()) {
                        modulesinfo_buy.isEnabled = true
                        modulesinfo_buy.setBackgroundColor(
                                resources.getColor(R.color.colorAccent)) }
                        if (isInstalled() || isInInventory()) {
                            modulesinfo_sell.isEnabled = true
                            modulesinfo_sell.setBackgroundColor(resources.getColor(R.color.colorAccent))
                        }
                        Snackbar.make(
                                modulesinfo_parentLayout,
                                "The item was successfully sold",
                                Snackbar.LENGTH_SHORT)
                                .show()
                    }
        }
    }

    override fun onStart() {
        super.onStart()
        userDocumentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        userDocumentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList = doc.toObject(User::class.java)!!
                modulesinfo_user_cash.text = userList.money.toString()
                if (isInstalled()) { setOnSellButtonVisibility(true)
                //    setOnBuyButtonVisibility(true)
                return@addSnapshotListener } else setOnSellButtonVisibility(false)
                if (!isEnoughMoneyToBuy()) setOnBuyButtonVisibility(false)
            }
        }

        armingDocumentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
                .collection("Inventory").document("arming")
        armingDocumentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                armingList = doc.toObject(User::class.java)!!
            }
        }
//        modulesDocumentReference!!.addSnapshotListener(
//                this
//        ) { doc, e ->
//            if (doc!!.exists()) {
//                currentModule = doc.getString("weaponName")
//                Log.i("myLogs", "current Module " + currentModule!!)
//                setOnBuySellButtonVisibility(currentModule!!)
//            } else {
//                setOnBuySellButtonVisibility("")
//            }
//        }
    }

    private fun isInstalled(): Boolean {
        return userList.weapon!!.contains(Weapon(position.toLong()))
    }

    private fun isInInventory(): Boolean {
        return armingList.weapon!!.contains(Weapon(position.toLong()))
    }

    private fun isEnoughMoneyToBuy(): Boolean {
        return userList.money >= module!!.price
    }

    private fun isSlotAvailable(): Boolean {
        return userList.weapon!!.size < Utils.getCurrentShipInfo(userList.ship!!).weaponSlots
    }

    private fun setOnBuyButtonVisibility(state: Boolean) {
        if (state) {
            modulesinfo_buy.isEnabled = true
            modulesinfo_buy.setBackgroundColor(resources.getColor(R.color.colorAccent))
        } else {
            modulesinfo_buy.isEnabled = false
            modulesinfo_buy.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    private fun setOnSellButtonVisibility(state: Boolean) {
        if (state) {
            modulesinfo_sell.isEnabled = true
            modulesinfo_sell.setBackgroundColor(resources.getColor(R.color.colorAccent))
        } else {
            modulesinfo_sell.isEnabled = false
            modulesinfo_sell.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    fun onGoBackToMainOptions(view: View) {
        finish()
    }
}
