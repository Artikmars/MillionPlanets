package com.artamonov.millionplanets.fight.presenter

import com.artamonov.millionplanets.fight.FightActivityView
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Utils
import com.artamonov.millionplanets.utils.WeaponType
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Random

class FightActivityPresenterImpl(private var getView: FightActivityView) : FightActivityPresenter
<FightActivityView> {

    private var shieldDamage: Int = 0
    private var hpDamage: Int = 0
    private var enemyShieldDamage: Int = 0
    private var enemyHpDamage: Int = 0
    private var userList = User()
    private var enemyList = User()
    private var isFightFinished: Boolean = false
    private var isLootTransferFinished: Boolean = false

    override fun getUserList(): User {
        return userList
    }

    override fun fightFinished(): Boolean {
        return isFightFinished
    }

    override fun setLootTransferFinished(state: Boolean) {
        isLootTransferFinished = state
    }

    override fun setUserList(doc: DocumentSnapshot) {

        userList = doc.toObject(User::class.java)!!
        getView.setUserData(userList)
    }

    override fun setEnemyList(doc: DocumentSnapshot) {
        enemyList = doc.toObject(User::class.java)!!
        getView.setEnemyData(enemyList)
    }

    override fun calculateDamage() {
        calculateDamageToEnemy()
        calculateDamageFromEnemy()
        attack()
        if (isFinished()) {
            isFightFinished = true
            if (isYouWon()) {
                getView.showYouWonMessage()
            } else {
                getView.showEnemyWonMessage()
            }
        } else {
            getView.startTimer()
        }
    }

    private fun isYouWon(): Boolean {
        return userList.hp != 0L
    }

    override fun calculateLoot() {
        if (isYouWon()) {
            getView.calculateLoot(userList.nickname, enemyList.nickname, enemyList.ship)
            getView.showLootSnackbar(isYouWon(), enemyList.ship)
        } else {
            getView.calculateLoot(enemyList.nickname, userList.nickname, userList.ship)
            getView.showLootSnackbar(isYouWon(), userList.ship) }
    }

    private fun isFinished(): Boolean {
        return userList.hp == 0L || enemyList.hp == 0L
    }

    override fun calculateDamageToEnemy() {
        hpDamage = 0
        shieldDamage = 0

        for (weapon in userList.weapon!!.indices) {
            when (Utils.getCurrentModuleInfo(userList.weapon!![weapon])?.type) {
                WeaponType.LASER.name -> { shieldDamage += randomizeDamage(userList.damage!![weapon])
                    hpDamage += randomizeDamage(userList.damage!![weapon]) }
                WeaponType.GUN.name -> {
                    val randomizedDamage = randomizeDamage(userList.damage!![weapon])
                    shieldDamage += randomizedDamage / 2
                    hpDamage += randomizedDamage / 2 }
            }
        }
    }

    override fun calculateDamageFromEnemy() {
        enemyHpDamage = 0
        enemyShieldDamage = 0
        for (weapon in enemyList.weapon!!.indices) {
            when (Utils.getCurrentModuleInfo(enemyList.weapon!![weapon])?.type) {
                WeaponType.LASER.name ->
                {
                    enemyShieldDamage += randomizeDamage(enemyList.damage!![weapon])
                    enemyHpDamage += randomizeDamage(enemyList.damage!![weapon])
                }
                WeaponType.GUN.name -> {
                    val randomizedDamage = randomizeDamage(enemyList.damage!![weapon])
                    enemyShieldDamage += randomizedDamage / 2
                    enemyHpDamage += randomizedDamage / 2
                }
            }
        }
    }

    private fun attack() {
        if (enemyList.shield != 0L) {
        enemyList.shield = enemyList.shield - shieldDamage
            if (enemyList.shield < 0) enemyList.shield = 0
        } else {
            enemyList.hp = enemyList.hp - hpDamage
            if (enemyList.hp < 0) enemyList.hp = 0
        }

        if (userList.shield != 0L) {
            userList.shield = userList.shield - enemyShieldDamage
            if (userList.shield < 0) userList.shield = 0
        } else {
            userList.hp = userList.hp - enemyHpDamage
            if (userList.hp < 0) userList.hp = 0
        }
        getView.setUserData(userList)
        getView.setEnemyData(enemyList)
        getView.setFightLog(hpDamage, shieldDamage, enemyHpDamage, enemyShieldDamage)
    }

    private fun randomizeDamage(damage: Long): Int {
        return Random().nextInt(((damage.toInt() + (damage.toInt() / 100)*25) -
                (damage.toInt() - (damage.toInt() / 100)*25) + 1) + (damage.toInt() - (damage.toInt() / 100)*25))
    }
}