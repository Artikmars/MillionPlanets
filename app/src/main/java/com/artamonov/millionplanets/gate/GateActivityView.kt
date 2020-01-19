package com.artamonov.millionplanets.gate

import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentSnapshot

interface GateActivityView {

    fun debrisIsRemoved()

    fun mineDebris()

    fun mineIron()

    fun openFightActivity(enemyUsername: String?)

    fun openPlanetActivity()

    fun setFightType()

    fun setProgressBar()

    fun setUserData(userList: User)

    fun setUserIron(userList: User, documentSnapshot: DocumentSnapshot?)

    fun showCapacityError()

    fun showNotEnoughMoneyToBuyFuelWarning()

    fun showUpdateIronToast(counter: Int, totalAmount: Long)
}
