package com.artamonov.millionplanets.inventory.presenter

import com.artamonov.millionplanets.inventory.InventoryActivityView
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.google.firebase.firestore.DocumentSnapshot

interface InventoryActivityPresenter<V : InventoryActivityView> {

    fun getCargoAmount(): Int

    fun getAvailablePetrolAmountToBeFilled(): Long?

    fun getWeaponAmount(): Int

    fun getObjectModel(): ObjectModel?

    fun getUserList(): User

    fun getCargoItem(position: Int): Item

    fun getWeaponItem(position: Int): Long

    fun getCargoList(): List<Item>?

    fun getResourceAmount(position: Int): Long?

    fun getWeaponList(): List<Weapon>

    fun initData()

    fun initFirebase()

    fun initUserList(doc: DocumentSnapshot)

    fun isFuelFull(): Boolean

    fun isInstalled(position: Int): Boolean

    fun isPetrolAvailable(): Boolean

    fun setEnabled(position: Int, state: Boolean)
}
