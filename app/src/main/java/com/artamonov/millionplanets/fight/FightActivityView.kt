package com.artamonov.millionplanets.fight

import com.artamonov.millionplanets.model.User

interface FightActivityView {
    fun calculateLoot(nicknameGet: String, nicknameLose: String, ship: String)
    fun setFightLog(hpDamage: Int, shieldDamage: Int, enemyHpDamage: Int, enemyShieldDamage: Int)
    fun setProgressBar(state: Boolean)
    fun setUserData(userList: User)
    fun setEnemyData(userList: User)
    fun showLootSnackbar(isYouWon: Boolean, ship: String)
    fun showYouWonMessage()
    fun showEnemyWonMessage()
    fun startTimer()
}