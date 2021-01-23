package com.artamonov.millionplanets.inventory.presenter

import com.artamonov.millionplanets.inventory.InventoryActivityView
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.artamonov.millionplanets.utils.getShipFuelInfo
import com.google.firebase.firestore.DocumentSnapshot

class InventoryActivityPresenterImpl(private var getView: InventoryActivityView) : InventoryActivityPresenter<InventoryActivityView> {
    private var userList: User = User()
    private var weaponList: List<Weapon> = listOf()
    private var cargoList: List<Item> = listOf()
    private var objectModel: SpaceObject = SpaceObject()

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

    override fun getCargoItem(position: Int): Item {
        return cargoList[position]
    }

    override fun getObjectModel(): SpaceObject? {
        return objectModel
    }

    override fun initUserList(doc: DocumentSnapshot) {
        userList = doc.toObject(User::class.java)!!
        weaponList = userList.weapon!!
        cargoList = userList.cargo!!
        getView.updateCargoCapacityCounter(userList)
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

    override fun getAvailablePetrolAmountToBeFilled(): Long? {
        return if (cargoList.find { it.itemId == 5L }?.itemAmount!! <
                (getShipFuelInfo(userList.ship!!) - userList.fuel!!))
            cargoList.find { it.itemId == 5L }?.itemAmount!! else
            getShipFuelInfo(userList.ship!!) - userList.fuel!!
    }

    override fun isPetrolAvailable(): Boolean {
        return cargoList.find { it.itemId == 5L && it.itemAmount!! > 0 } != null
    }

    override fun isFuelFull(): Boolean {
        return userList.fuel!! >= getShipFuelInfo(userList.ship!!)
    }
}