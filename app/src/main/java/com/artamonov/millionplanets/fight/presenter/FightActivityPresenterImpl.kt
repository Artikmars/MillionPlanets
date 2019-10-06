package com.artamonov.millionplanets.move.presenter

import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.move.MoveActivityView
import com.artamonov.millionplanets.utils.Utils
import com.google.firebase.firestore.DocumentSnapshot

class FightActivityPresenterImpl(private var getView: MoveActivityView) : MoveActivityPresenter<MoveActivityView> {

    override fun getFuel() {
        val fuelToFill = Utils.getShipFuelInfo(userList.ship) - userList.fuel
        val price = fuelToFill * 1000
        if (userList.money >= price) {

            getView.buyFuel(Utils.getShipFuelInfo(userList.ship), userList.money - price)
            getView.setProgressBar(false)
        } }

    internal var userList = User()

    override fun getUserList(): User {
        return userList
    }

    override fun setUserList(doc: DocumentSnapshot) {
        userList.ship = doc.getString("ship")
        userList.x = doc.getLong("x")?.toInt() ?: 0
        userList.y = doc.getLong("y")?.toInt() ?: 0
        userList.sumXY = doc.getLong("sumXY")?.toInt() ?: 0
        userList.hp = doc.getLong("hp")?.toInt() ?: 0
        userList.cargo = doc.getLong("cargo")?.toInt() ?: 0
        userList.fuel = doc.getLong("fuel")?.toInt() ?: 0
        userList.scanner_capacity = doc.getLong("scanner_capacity")?.toInt() ?: 0
        userList.shield = doc.getLong("shield")?.toInt() ?: 0
        userList.money = doc.getLong("money")?.toInt() ?: 0
        userList.moveToObjectName = doc.getString("moveToObjectName")
        userList.moveToObjectDistance = doc.getLong("moveToObjectDistance")!!.toInt()
    }

    override fun ifEnoughFuelToJump() {
        if (userList.fuel == 0) {
        getView.setSnackbarError(R.string.run_out_of_fuel)
        return
    }
        if (userList.fuel - userList.moveToObjectDistance < 0) { getView.setSnackbarError(R.string.not_enough_fuel_to_get_to_destination) }
    }
}