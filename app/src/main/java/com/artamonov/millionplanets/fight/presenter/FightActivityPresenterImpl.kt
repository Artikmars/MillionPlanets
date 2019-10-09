package com.artamonov.millionplanets.fight.presenter

import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.fight.FightActivityView
import com.artamonov.millionplanets.model.User
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

    override fun getUserList(): User {
        return userList
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
            if (isYouWon()) {
                getView.showYouWonMessage()
            } else {
                getView.showEnemyWonMessage()
            }
        }
    }

    private fun isYouWon(): Boolean {
        return userList.hp != 0
    }

    private fun isFinished(): Boolean {
        return (userList.hp == 0 || enemyList.hp == 0)
    }

    override fun calculateDamageToEnemy() { for (weapon in userList.weaponType!!.indices) {
        when (userList.weaponType!![weapon]) {
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
        for (weapon in enemyList.weaponType!!.indices) {
            when (enemyList.weaponType!![weapon]) {
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
        if (enemyList.shield != 0) {
        enemyList.shield = enemyList.shield - shieldDamage
            if (enemyList.shield < 0) enemyList.shield = 0
        } else {
            enemyList.hp = enemyList.hp - hpDamage
            if (enemyList.hp < 0) enemyList.hp = 0
        }

        if (userList.shield != 0) {
            userList.shield = userList.shield - enemyShieldDamage
            if (userList.shield < 0) userList.shield = 0
        } else {
            userList.hp = userList.hp - enemyHpDamage
            if (userList.hp < 0) userList.hp = 0
        }
        getView.setUserData(userList)
        getView.setEnemyData(enemyList)
    }

    private fun randomizeDamage(damage: Int): Int {
        return Random().nextInt(((damage + (damage / 100)*25) -
                (damage - (damage / 100)*25) + 1) + (damage - (damage / 100)*25))
    }

    override fun ifEnoughFuelToJump() {
        if (userList.fuel == 0) {
        getView.setSnackbarError(R.string.run_out_of_fuel)
        return
    }
        if (userList.fuel - userList.moveToObjectDistance < 0) { getView.setSnackbarError(R.string.not_enough_fuel_to_get_to_destination) }
    }
}