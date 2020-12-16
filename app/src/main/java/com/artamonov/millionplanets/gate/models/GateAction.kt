package com.artamonov.millionplanets.gate.models

import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentSnapshot

sealed class GateAction {
    data class ShowUpdateIronToast(val counter: Int, val totalAmount: Long) : GateAction()
    object ShowMineFailedSnackbar : GateAction()
    data class SetUserIron(val userList: User, val documentSnapshot: DocumentSnapshot?) : GateAction()
    data class OpenFightActivity(val enemyUsername: String?) : GateAction()
}
