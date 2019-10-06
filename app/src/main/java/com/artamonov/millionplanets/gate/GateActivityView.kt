package com.artamonov.millionplanets.gate

import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentSnapshot

interface GateActivityView {

    fun buyFuel(fuel: Int, money: Int, ship: String?)

    fun mineDebris()

    fun mineIron()

    fun openPlanetActivity()

    fun setFightType()

    fun setProgressBar()

    fun setUserData(userList: User)

    fun setUserIron(userList: User, documentSnapshot: DocumentSnapshot?)

    fun updateIron(counter: Int)
}
