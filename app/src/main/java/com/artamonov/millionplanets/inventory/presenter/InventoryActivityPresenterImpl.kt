package com.artamonov.millionplanets.inventory.presenter

import com.artamonov.millionplanets.inventory.InventoryActivityView
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class InventoryActivityPresenterImpl(private var getView: InventoryActivityView) : InventoryActivityPresenter<InventoryActivityView> {
    internal var firebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseUser: FirebaseUser? = null
    private var userList: User = User()
    private var weaponList: List<Weapon> = listOf()
    private var cargoList: List<Item> = listOf()
    private var objectModel: ObjectModel = ObjectModel()
    private var userDocument: DocumentReference? = null

    override fun initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    override fun getUserList(): User {
        return userList
    }

    override fun getWeaponAmount(): Int {
//        val cargoListShort = cargoList.toMutableList()
//        cargoListShort.remove(0)

//        var counter = 0
//        for (index in 0..9) { if (cargoList[index] != 0L) counter++ }
//        return counter
        return weaponList.size
    }

    override fun getCargoAmount(): Int {
//        val cargoListShort = cargoList.toMutableList()
//        cargoListShort.remove(0)

//        var counter = 0
//        for (index in 0..9) { if (cargoList[index] != 0L) counter++ }
//        return counter
        return cargoList.size
    }

    override fun getCargoList(): List<Item>? {
        return cargoList
    }

    override fun getWeaponList(): List<Weapon> {
        return weaponList
    }

    override fun getWeaponItem(position: Int): Long {
        return weaponList[position].weaponId!!
    }

    override fun getCargoItem(position: Int): Long {
        return cargoList[position].itemId!!
    }

    override fun getObjectModel(): ObjectModel? {
        return objectModel
    }

    override fun initData() {
        userDocument = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
    }

    override fun initUserList(doc: DocumentSnapshot) {
        userList = doc.toObject(User::class.java)!!
        weaponList = userList.weapon!!
        cargoList = userList.cargo!!
    }

    override fun isInstalled(position: Int): Boolean {
        return weaponList[position].isWeaponInstalled!!
    }

    override fun setEnabled(position: Int, state: Boolean) {
        weaponList[position].isWeaponInstalled = state
        getView.updateData()
    }

    override fun getResourceAmount(position: Int): Long? {
        return cargoList[position].itemAmount
    }
}