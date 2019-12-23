package com.artamonov.millionplanets.move.presenter

import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.move.MoveActivityView
import com.artamonov.millionplanets.utils.Utils
import com.google.firebase.firestore.DocumentSnapshot

class MoveActivityPresenterImpl(private var getView: MoveActivityView) : MoveActivityPresenter<MoveActivityView> {

    override fun getFuel() {
        val fuelToFill = Utils.getShipFuelInfo(userList.ship!!) - userList.fuel
        val price = fuelToFill * 1000
        if (userList.money >= price) {

            getView.buyFuel(Utils.getShipFuelInfo(userList.ship!!), userList.money - price)
            getView.setProgressBar(false)
        } }

    internal var userList = User()

    override fun getUserList(): User {
        return userList
    }

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

    override fun ifEnoughFuelToJump() {
        if (userList.fuel == 0L) {
        getView.setSnackbarError(R.string.run_out_of_fuel)
        return
    }
        if (userList.fuel - userList.moveToObjectDistance < 0) { getView.setSnackbarError(R.string.not_enough_fuel_to_get_to_destination) }
    }
}