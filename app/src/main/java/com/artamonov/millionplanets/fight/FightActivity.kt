package com.artamonov.millionplanets.fight

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference

import com.artamonov.millionplanets.MainOptionsActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.scanresult.ScanResultActivity
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenter
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenterImpl
import com.artamonov.millionplanets.gate.GateActivity.Companion.ENEMY_USERNAME
import com.artamonov.millionplanets.inventory.InventoryActivity
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Utils
import kotlinx.android.synthetic.main.fight_activity.*
import kotlinx.android.synthetic.main.move_activity.progressBar

class FightActivity : BaseActivity(R.layout.fight_activity), FightActivityView {
    private var parentLayout: View? = null
    private var userDocument: DocumentReference? = null
    private var enemyDocument: DocumentReference? = null
    private var enemyUsername: String? = null
    private var countDownTimer: CountDownTimer? = null
    private var remainedSecs: Long = 0
    private var user: User = User()
    private var enemy: User = User()

    lateinit var presenter: FightActivityPresenter<FightActivityView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = FightActivityPresenterImpl(this)

        if (intent != null) {
            enemyUsername = intent.getStringExtra(ENEMY_USERNAME)
            enemy_label.text = enemyUsername
        }

        fight.setOnClickListener {
            if (presenter.fightFinished()) presenter.calculateLoot() else
            presenter.calculateDamage()
        }

        retreat.setOnClickListener {
            val intent = Intent(this, ScanResultActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        userDocument = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
        userDocument!!.addSnapshotListener(this) { doc, _ ->
            if (doc!!.exists()) {
                user = doc.toObject(User::class.java)!!
                presenter.setUserList(doc)
                enemyDocument = firebaseFirestore.collection("Objects")
                        .document(presenter.userList.locationName!!)
                enemyDocument!!.addSnapshotListener(this) { doc2, _ ->
                    if (doc2!!.exists()) {
                        presenter.setEnemyList(doc2)
                    }
                }
            }
        }
    }

    override fun setUserData(userList: User) {
        ship_you.text = userList.ship
        hp_you.text = userList.hp.toString()
        shield_you.text = userList.shield.toString()
        weapon_you.text = userList.weapon?.size.toString() + "/3"
    }

    override fun setEnemyData(enemyList: User) {
        enemy_label.text = enemyList.nickname
        ship_enemy.text = enemyList.ship
        hp_enemy.text = enemyList.hp.toString()
        shield_enemy.text = enemyList.shield.toString()
        weapon_enemy.text = enemyList.weapon?.size.toString() + "/3"
    }

    override fun showYouWonMessage() {
        fight_log.text = "YOU WON!"
        fight_log.setTextColor(resources.getColor(R.color.colorAccent))
        fight.text = "Collect debris"
    }

    override fun showEnemyWonMessage() {
        fight_log.text = "YOU LOSE!"
        fight_log.setTextColor(resources.getColor(R.color.red))
        fight.text = "Give your debris"
    }

    override fun setFightLog(hpDamage: Int, shieldDamage: Int, enemyHpDamage: Int, enemyShieldDamage: Int) {
        fight_log.text = "You damaged " + hpDamage + " point to hp\n" + "You damaged " + shieldDamage + " point to shield\n" + presenter.userList.locationName + " damaged " + enemyHpDamage + " point to hp\n" + presenter.userList.locationName + " damaged " + enemyShieldDamage + " point to shield"
    }

    override fun startTimer() {
        fight.isClickable = false
        countDownTimer = object : CountDownTimer(15000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                remainedSecs = millisUntilFinished / 1000
                fight.text = resources.getString(R.string.fight) + " " + remainedSecs / 60 + ":" + remainedSecs % 60
            }

            override fun onFinish() {
                cancel()
                countDownTimer = null
                fight.text = resources.getString(R.string.fight)
                fight.isClickable = true
            }
        }.start()
    }

    override fun calculateLoot(usernameWhoGetLoot: String, usernameWhoLoseLoot: String, enemyShip: String) {
        val lootedIron: Long = Utils.getCurrentShipInfo(enemyShip).hp
        enemyDocument!!.get().addOnSuccessListener { documentSnapshot ->
            enemy = documentSnapshot.toObject(User::class.java)!!

            // user.cargo?.let { list1 -> enemy.cargo?.let(list1::addAll) }
            for (item in enemy.cargo!!) {
                val cargoItem = user.cargo?.find { it.itemId == item.itemId }
                if (cargoItem != null) {
                    user.cargo?.find { it.itemId == item.itemId }?.itemAmount?.plus(item.itemAmount!!)
                } else {
                    user.cargo?.add(Item(itemId = item.itemId, itemAmount = item.itemAmount))
                }
            }

            enemy.cargo = mutableListOf()

            lootIron(lootedIron)

            val batch = firebaseFirestore.batch()
            batch.update(userDocument!!, "cargo", user.cargo)
            batch.update(enemyDocument!!, "cargo", enemy.cargo)
            batch.commit()

            fight.visibility = View.GONE
            retreat.text = resources.getString(R.string.leave)
            presenter.setLootTransferFinished(true)
            checkIfCargoCapacityIsExceed()
            }
    }

    private fun lootIron(lootedIron: Long) {

        val item = user.cargo?.find { it.itemId == 4L }
        item?.let {
            val index = user.cargo?.indexOfFirst { it.itemId == 4L }
            user.cargo!![index!!].itemAmount = item.itemAmount!! + lootedIron
            return
            }

        val iron = Item(4, lootedIron)
        user.cargo?.add(iron)
        enemy.cargo = null
    }

    private fun checkIfCargoCapacityIsExceed() {
        val sum = user.cargo?.sumBy { it.itemAmount!!.toInt() }
        sum?.let {
            if (sum > user.cargoCapacity) {
                openInventoryActivity()
            }
        }
    }

    private fun openInventoryActivity() {
        val intent = Intent(this, InventoryActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
        finish()
    }

    override fun showLootSnackbar(isYouWon: Boolean, ship: String) {
        if (isYouWon) {
            setSnackbarError("You won " + Utils.getCurrentShipInfo(ship).hp + " iron!")
        } else {
            setSnackbarError("You lost " + Utils.getCurrentShipInfo(ship).hp + " iron.")
        }
    }

    override fun setProgressBar(state: Boolean) {
        progressBar.progress = 100
        progressBar.visibility = View.INVISIBLE
    }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    private fun setSnackbarError(errorMessage: String) {
        parentLayout = findViewById(android.R.id.content)
        Snackbar.make(parentLayout!!, errorMessage,
                Snackbar.LENGTH_LONG).show() }

    companion object {
        private val TAG = "myLogs"
    }
}
