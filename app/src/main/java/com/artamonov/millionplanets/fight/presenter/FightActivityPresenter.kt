package com.artamonov.millionplanets.fight.presenter

import com.artamonov.millionplanets.fight.FightActivityView
import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentSnapshot

interface FightActivityPresenter<V : FightActivityView?> {
    fun getUserList(): User
    fun calculateDamage()
    fun calculateDamageFromEnemy()
    fun calculateDamageToEnemy()
    fun calculateLoot()
    fun fightFinished(): Boolean
    fun setLootTransferFinished(state: Boolean)
    fun setUserList(doc: DocumentSnapshot)
    fun setEnemyList(doc: DocumentSnapshot)
}