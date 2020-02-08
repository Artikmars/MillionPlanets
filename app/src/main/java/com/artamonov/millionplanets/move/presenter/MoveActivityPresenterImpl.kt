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
        userList = doc.toObject(User::class.java)!!
    }

    override fun ifEnoughFuelToJump() {
        if (userList.fuel == 0L) {
        getView.setSnackbarError(R.string.run_out_of_fuel)
        return
    }
        if (userList.fuel - userList.moveToObjectDistance < 0) { getView.setSnackbarError(R.string.not_enough_fuel_to_get_to_destination) }
    }
}