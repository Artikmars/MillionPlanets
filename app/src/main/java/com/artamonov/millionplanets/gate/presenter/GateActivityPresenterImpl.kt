package com.artamonov.millionplanets.gate.presenter

import com.artamonov.millionplanets.gate.GateActivityView
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class GateActivityPresenterImpl(private var getView: GateActivityView) : GateActivityPresenter<GateActivityView> {

    internal var firebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseUser: FirebaseUser? = null
    private var userList: User = User()
    private var objectModel: ObjectModel = ObjectModel()
    private var userDocument: DocumentReference? = null
    private var planetDocument: DocumentReference? = null

    override fun initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    override fun getUserList(): User? {
        return userList
    }

    override fun getObjectModel(): ObjectModel? {
        return objectModel
    }
    override fun initData() {
        userDocument = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
        firebaseFirestore.runTransaction { transaction ->
            val documentSnapshot = transaction.get(userDocument!!)
            userList.moveToObjectName = documentSnapshot.getString("moveToObjectName")
            if (userList.moveToObjectName != null) {
                planetDocument = firebaseFirestore.collection("Objects")
                        .document(userList.moveToObjectName!!)
                val documentSnapshot2 = transaction.get(planetDocument!!)
                objectModel.debrisIronAmount = documentSnapshot2.getLong("iron")!!.toInt()
                transaction.update(userDocument!!, "moveToObjectName", userList.moveToObjectName)
                transaction.update(planetDocument!!, "iron", objectModel.debrisIronAmount) }
            null
        }
    }

    override fun updateIron(i: Int) {
        getView.updateIron(i) }

    override fun initUserList(doc: DocumentSnapshot) {
        userList.moveToObjectName = doc.getString("moveToObjectName")
        userList.ship = doc.getString("ship")
        userList.x = doc.getLong("x")!!
        userList.y = doc.getLong("y")!!
        userList.sumXY = doc.getLong("sumXY")!!
        userList.hp = doc.getLong("hp")!!
        userList.cargoCapacity = doc.getLong("cargoCapacity")!!
        userList.fuel = doc.getLong("fuel")!!
        userList.scanner_capacity = doc.getLong("scanner_capacity")!!
        userList.shield = doc.getLong("shield")!!
        userList.money = doc.getLong("money")!!
        userList.moveToObjectType = doc.getString("moveToObjectType")

        getView.setUserData(userList)
    }

    override fun getUserIron(documentSnapshot: DocumentSnapshot?) {
        getView.setUserIron(userList, documentSnapshot)
    }

    override fun getFuel(fuel: Int, money: Int) {
        val fuelToFill = Utils.getShipFuelInfo(userList.ship!!) - userList.fuel
        val price = fuelToFill * 1000
        if (userList.money >= price) {
            getView.buyFuel(Utils.getShipFuelInfo(userList.ship!!), userList.money - price, userList.ship)
        } }

    override fun setUserList(doc: DocumentSnapshot) {
        userList.ship = doc.getString("ship")
        userList.x = doc.getLong("x") ?: 0
        userList.y = doc.getLong("y") ?: 0
        userList.sumXY = doc.getLong("sumXY") ?: 0
        userList.hp = doc.getLong("hp") ?: 0
        userList.cargoCapacity = doc.getLong("cargoCapacity") ?: 0
        userList.fuel = doc.getLong("fuel") ?: 0
        userList.scanner_capacity = doc.getLong("scanner_capacity") ?: 0
        userList.shield = doc.getLong("shield") ?: 0
        userList.money = doc.getLong("money") ?: 0
        userList.moveToObjectName = doc.getString("moveToObjectName")
        userList.moveToObjectDistance = doc.getLong("moveToObjectDistance")!!
    }

    override fun setObjectType() {
        when (userList.moveToObjectType) {
            "planet" -> {
                getView.openPlanetActivity()
            }
            "user" -> getView.setFightType()
            "fuel" -> getView.buyFuel(userList.fuel, userList.money, userList.ship)
            "debris" -> getView.mineDebris()
            "meteorite_field" -> getView.mineIron()
        }
    }

    override fun prepareData() {
        firebaseFirestore.runTransaction { transaction ->
            val documentSnapshot = transaction.get(userDocument!!)
            userList.moveToObjectName = documentSnapshot.getString("moveToObjectName")
            if (userList.moveToObjectName != null) {
                planetDocument = firebaseFirestore.collection("Objects")
                        .document(userList.moveToObjectName!!)
                val documentSnapshot2 = transaction.get(planetDocument!!)
                objectModel.debrisIronAmount = documentSnapshot2.getLong("iron")!!.toInt()
                transaction.update(userDocument!!, "moveToObjectName", userList.moveToObjectName)
                transaction.update(planetDocument!!, "iron", objectModel.debrisIronAmount) }
            null
        }
    }
}