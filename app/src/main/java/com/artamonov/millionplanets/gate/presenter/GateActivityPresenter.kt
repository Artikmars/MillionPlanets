package com.artamonov.millionplanets.gate.presenter

import com.artamonov.millionplanets.gate.GateActivityView
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentSnapshot

interface GateActivityPresenter<V : GateActivityView> {

    fun getObjectModel(): SpaceObject?

    fun getUserIron(documentSnapshot: DocumentSnapshot?)

    fun getUserList(): User?

    fun initData()

    fun initFirebase()

    fun setObjectType()

    fun updateIron(newAmount: Int)
}
