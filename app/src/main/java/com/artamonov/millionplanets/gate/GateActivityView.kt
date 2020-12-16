package com.artamonov.millionplanets.gate

import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentSnapshot

interface GateActivityView {

    fun setUserIron(userList: User, documentSnapshot: DocumentSnapshot?)

    fun showUpdateIronToast(counter: Int, totalAmount: Long)
}
