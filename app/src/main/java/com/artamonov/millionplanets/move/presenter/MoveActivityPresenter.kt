package com.artamonov.millionplanets.move.presenter

import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.move.MoveActivityView
import com.google.firebase.firestore.DocumentSnapshot

interface MoveActivityPresenter<V : MoveActivityView?> {
    fun fillFuel()
    fun getUserList(): User
    fun setUserList(doc: DocumentSnapshot)
    fun ifEnoughFuelToJump()
}