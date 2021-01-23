package com.artamonov.millionplanets.fight

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import androidx.core.content.ContextCompat

import com.google.firebase.firestore.DocumentReference

import com.artamonov.millionplanets.MainOptionsActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.scanresult.ScanResultActivity
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityFightBinding
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenter
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenterImpl
import com.artamonov.millionplanets.gate.GateActivity.Companion.ENEMY_USERNAME
import com.artamonov.millionplanets.inventory.InventoryActivity
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.getCurrentShipInfo
import com.artamonov.millionplanets.utils.showSnackbarError

class FightActivity : BaseActivity(), FightActivityView {
    private var enemyDocument: DocumentReference? = null
    private var enemyUsername: String? = null
    private var countDownTimer: CountDownTimer? = null
    private var remainedSecs: Long = 0
    private var user: User = User()
    private var enemy: User = User()

    lateinit var presenter: FightActivityPresenter<FightActivityView>
    private lateinit var binding: ActivityFightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFightBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        presenter = FightActivityPresenterImpl(this)

        if (intent != null) {
            enemyUsername = intent.getStringExtra(ENEMY_USERNAME)
            binding.enemyLabel.text = enemyUsername
        }

        binding.fight.setOnClickListener {
            if (presenter.fightFinished()) presenter.calculateLoot() else
            presenter.calculateDamage()
        }

        binding.retreat.setOnClickListener {
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
        userDocument.addSnapshotListener(this) { doc, _ ->
            if (doc!!.exists()) {
                user = doc.toObject(User::class.java)!!
                presenter.setUserList(doc)
                enemyDocument = firebaseFirestore.collection("Objects")
                        .document(presenter.getUserList().locationName!!)
                enemyDocument!!.addSnapshotListener(this) { doc2, _ ->
                    if (doc2!!.exists()) {
                        presenter.setEnemyList(doc2)
                    }
                }
            }
        }
    }

    override fun setUserData(userList: User) {
        with(binding) {
            shipYou.text = userList.ship
            hpYou.text = userList.hp.toString()
            shieldYou.text = userList.shield.toString()
            weaponYou.text = userList.weapon?.size.toString() + "/3"
        }
    }

    override fun setEnemyData(enemyList: User) {
        with(binding) {
            enemyLabel.text = enemyList.nickname
            shipEnemy.text = enemyList.ship
            hpEnemy.text = enemyList.hp.toString()
            shieldEnemy.text = enemyList.shield.toString()
            weaponEnemy.text = enemyList.weapon?.size.toString() + "/3"
        }
    }

    override fun showYouWonMessage() {
        with(binding) {
            fightLog.text = "YOU WON!"
            fightLog.setTextColor(ContextCompat.getColor(this@FightActivity, R.color.colorAccent))
            fight.text = "Collect debris"
        }
    }

    override fun showEnemyWonMessage() {
        binding.fightLog.text = "YOU LOSE!"
        binding.fightLog.setTextColor(ContextCompat.getColor(this@FightActivity, R.color.red))
        binding.fight.text = "Give your debris"
    }

    override fun setFightLog(hpDamage: Int, shieldDamage: Int, enemyHpDamage: Int, enemyShieldDamage: Int) {
        binding.fightLog.text = "You damaged " + hpDamage + " point to hp\n" + "You damaged " +
                shieldDamage + " point to shield\n" + presenter.getUserList().locationName +
                " damaged " + enemyHpDamage + " point to hp\n" + presenter.getUserList().locationName +
                " damaged " + enemyShieldDamage + " point to shield"
    }

    override fun startTimer() {
        binding.fight.isClickable = false
        countDownTimer = object : CountDownTimer(15000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                remainedSecs = millisUntilFinished / 1000
                binding.fight.text = resources.getString(R.string.fight) + " " + remainedSecs / 60 + ":" + remainedSecs % 60
            }

            override fun onFinish() {
                cancel()
                countDownTimer = null
                binding.fight.text = resources.getString(R.string.fight)
                binding.fight.isClickable = true
            }
        }.start()
    }

    override fun calculateLoot(usernameWhoGetLoot: String, usernameWhoLoseLoot: String, enemyShip: String) {
        val lootedIron: Long = getCurrentShipInfo(enemyShip).hp!!
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
            batch.update(userDocument, "cargo", user.cargo)
            batch.update(enemyDocument!!, "cargo", enemy.cargo)
            batch.commit()

            binding.fight.visibility = GONE
            binding.retreat.text = resources.getString(R.string.leave)
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
            if (sum > user.cargoCapacity!!) {
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
            showSnackbarError("You won " + getCurrentShipInfo(ship).hp + " iron!")
        } else {
            showSnackbarError("You lost " + getCurrentShipInfo(ship).hp + " iron")
        }
    }

    override fun setProgressBar(state: Boolean) {
        binding.progressBar.progress = 100
        binding.progressBar.visibility = INVISIBLE
    }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    companion object {
        private val TAG = "myLogs"
    }
}
