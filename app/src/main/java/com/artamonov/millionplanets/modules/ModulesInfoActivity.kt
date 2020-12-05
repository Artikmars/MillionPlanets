package com.artamonov.millionplanets.modules

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityModulesInfoBinding
import com.artamonov.millionplanets.model.Module
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.artamonov.millionplanets.utils.getCurrentModuleInfo
import com.artamonov.millionplanets.utils.getCurrentShipInfo
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.firebase.firestore.DocumentReference

class ModulesInfoActivity : BaseActivity() {
    private var userList = User()
    private var userDocumentReference: DocumentReference? = null
    private var module: Module? = null
    private var position: Int = 0

    lateinit var binding: ActivityModulesInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModulesInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        position = intent.getIntExtra("position", -1)

        userDocumentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)

        module = position.getCurrentModuleInfo()
        binding.modulesinfoClass.text = module?.moduleClass
        binding.modulesinfoCost.text = module?.price.toString()
        binding.modulesinfoDamageHp.text = module?.damageHP.toString()
        binding.modulesinfoCost.text = module?.price.toString()

        binding.modulesinfoBuy.setOnClickListener {

            if (!isEnoughMoneyToBuy()) {
                showSnackbarError(getString(R.string.modules_info_not_enough_money))
                return@setOnClickListener
            }

            val batch = firebaseFirestore.batch()
            userList.weapon?.add(Weapon(position.toLong(), isSlotAvailable()))
            batch.update(userDocumentReference!!, "weapon", userList.weapon)

//                val weaponType = userList.weaponType!!.toMutableList()
//                weaponType.add(weaponType.size, Utils.getCurrentModuleInfo(position.toLong())!!.type)

//            else {
//                    if (armingList.weapon != null) {
//                        val weapon = armingList.weapon
//                        weapon?.add(Weapon(position.toLong(), true))
//                        batch.update(armingDocumentReference!!, "weapon", weapon)
//                    } else {
//                        val weapon = mutableListOf<Weapon>()
//                        weapon.add(Weapon(position.toLong(), true))
//                        armingList.weapon = weapon
//                        batch.set(armingDocumentReference!!, weapon)
//                    }
//            }

//            val moduleMap = HashMap<String, Any>()
//            moduleMap["damageHP"] = module!!.damageHP
//            moduleMap["weaponClass"] = module!!.moduleClass
//            moduleMap["weaponName"] = module!!.name
            batch.update(userDocumentReference!!, "money", userList.money!! - module?.price!!)
            batch.commit()
                    .addOnCompleteListener {
//                        modulesinfo_buy.isEnabled = false
//                        modulesinfo_buy.setBackgroundColor(resources.getColor(R.color.grey))
                        binding.modulesinfoSell.isEnabled = true
                        binding.modulesinfoSell.setBackgroundColor(
                                resources.getColor(R.color.colorAccent))
                        showSnackbarError(getString(R.string.modules_info_item_was_bought_successfully))
                    }
        }

        binding.modulesinfoSell.setOnClickListener {
            val batch = firebaseFirestore.batch()
            // userList.weapon.removeAt(userList.weapon.indexOf(Weapon(position.toLong())))
            userList.weapon?.removeAll { it.weaponId == position.toLong() }
            batch.update(userDocumentReference!!, "weapon", userList.weapon)
            //    when {
//                isInInventory() -> {
//                    val weapon = armingList.weapon
//                    weapon.removeAt(weapon.indexOf(Weapon(position.toLong())))
//                    batch.update(armingDocumentReference!!, "weapon", weapon)
//                }
//                isInstalled() -> {
//                    val weapon = userList.weapon
//                    weapon.removeAt(weapon.indexOf(Weapon(position.toLong())))
//                    batch.update(userDocumentReference!!, "weapon", weapon)
//                }
//                else -> Snackbar.make(
//                        modulesinfo_parentLayout,
//                        "The item has not been found",
//                        Snackbar.LENGTH_SHORT)
//                        .show() //   }

//            val moduleMap = HashMap<String, Any>()
//            moduleMap["damageHP"] = Utils.getCurrentModuleInfo(0L)!!.damageHP
//            moduleMap["weaponClass"] = Utils.getCurrentModuleInfo(0L)!!.moduleClass
//            moduleMap["weaponName"] = Utils.getCurrentModuleInfo(0L)!!.name
            batch.update(userDocumentReference!!, "money", userList.money!! + module?.price!! / 2)
            batch.commit()
                    .addOnCompleteListener {
                        if (isEnoughMoneyToBuy()) {
                            binding.modulesinfoBuy.isEnabled = true
                            binding.modulesinfoBuy.setBackgroundColor(
                                resources.getColor(R.color.colorAccent)) }
                        if (isInInventory()) {
                            binding.modulesinfoSell.isEnabled = true
                            binding.modulesinfoSell.setBackgroundColor(resources.getColor(R.color.colorAccent))
                        }
                        showSnackbarError(getString(R.string.modules_info_item_was_sold_successfully))
                    }
        }
    }

    override fun onStart() {
        super.onStart()
        userDocumentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)
        userDocumentReference?.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList = doc.toObject(User::class.java)!!
                binding.modulesinfoUserCash.text = userList.money.toString()
                setOnSellButtonVisibility(isInstalled())
                setOnBuyButtonVisibility(isEnoughMoneyToBuy() && !isInstalled())
            }
        }

//        armingDocumentReference = firebaseFirestore.collection("Objects")
//                .document(firebaseUser!!.displayName!!)
//                .collection("Inventory").document("arming")
//        armingDocumentReference!!.addSnapshotListener(
//                this
//        ) { doc, _ ->
//            if (doc!!.exists()) {
//                armingList = doc.toObject(User::class.java)!!
//            }
//        }
//

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
        return userList.weapon!!.any { it.weaponId == position.toLong() }
    }

    private fun isInInventory(): Boolean {
        return userList.weapon!!.contains(Weapon(position.toLong()))
    }

    private fun isEnoughMoneyToBuy(): Boolean {
        return userList.money!! >= module?.price!!
    }

    private fun isSlotAvailable(): Boolean {
        return userList.weapon?.filter { it.isWeaponInstalled == true }?.size!! < getCurrentShipInfo(userList.ship!!).weaponSlots!!
    }

    private fun setOnBuyButtonVisibility(state: Boolean) {
        binding.modulesinfoBuy.isEnabled = state
        if (state) {
            binding.modulesinfoBuy.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        } else {
            binding.modulesinfoBuy.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
        }
    }

    private fun setOnSellButtonVisibility(state: Boolean) {
        binding.modulesinfoSell.isEnabled = state
        if (state) {
            binding.modulesinfoSell.setBackgroundColor(resources.getColor(R.color.colorAccent))
        } else {
            binding.modulesinfoSell.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    fun onGoBackToMainOptions(view: View) {
        finish()
    }
}
