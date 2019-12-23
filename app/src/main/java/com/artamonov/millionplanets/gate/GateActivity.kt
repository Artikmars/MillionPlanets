package com.artamonov.millionplanets.gate

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.artamonov.millionplanets.MainOptionsActivity
import com.artamonov.millionplanets.PlanetActivity
import com.artamonov.millionplanets.R

import com.artamonov.millionplanets.utils.RandomUtils
import com.google.firebase.firestore.DocumentReference

import java.util.HashMap
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.gate.presenter.GateActivityPresenter
import com.artamonov.millionplanets.gate.presenter.GateActivityPresenterImpl
import com.artamonov.millionplanets.ScanResultActivity
import com.artamonov.millionplanets.fight.FightActivity
import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.gate.*

class GateActivity : BaseActivity(), GateActivityView {

    private var documentReference: DocumentReference? = null
    private var inventoryDocument: DocumentReference? = null
    private var planetDocument: DocumentReference? = null
    private var maxTimeInMilliseconds: Long = 0
    private var debrisIsOver: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var remainedSecs: Long = 0
    private var enemyUsername: String? = null

    lateinit var presenter: GateActivityPresenter<GateActivityView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gate)
        presenter = GateActivityPresenterImpl(this)

        if (intent != null) {
            enemyUsername = intent.getStringExtra(ENEMY_USERNAME)
        }

        gate_action.setOnClickListener {

            if (countDownTimer != null) {
                countDownTimer!!.cancel()
                countDownTimer = null

                // maxTimeInMilliseconds/1000 indicates how much resources are going to be mined until the cargoCapacity is fulled
                inventoryDocument?.update("Iron", presenter.getUserList()!!.resource_iron + maxTimeInMilliseconds / 1000 - remainedSecs)
                planetDocument?.update("iron", presenter.getObjectModel()!!.debrisIronAmount - (maxTimeInMilliseconds / 1000 - remainedSecs))

                gate_action.text = resources.getString(R.string.mine)
                return@setOnClickListener
            }
            openFightActivity(enemyUsername)
        }
    }

    override fun buyFuel(fuel: Long, money: Long, ship: String?) {
        var maxFuel = 20
        when (ship) {
            getString(R.string.research_spaceship) -> maxFuel = 150
            getString(R.string.trader) -> maxFuel = 50
        }

        val fuelToFill = maxFuel - fuel
        val price = fuelToFill * 1000
        if (money >= price) {
            documentReference!!.update("fuel", maxFuel)
            documentReference!!.update("money", money - price)
        } }

    override fun openPlanetActivity() {
        val intent = Intent(this, PlanetActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            finish()
        } else {
            startActivity(intent)
            finish()
        }
    }

    private fun openFightActivity(enemyUsername: String?) {
        val intent = Intent(this, FightActivity::class.java)
        intent.putExtra(ENEMY_USERNAME, enemyUsername)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    override fun setFightType() {
        gate_action.text = resources.getString(R.string.fight_with, presenter.getUserList()?.moveToObjectName)
    }

    override fun onStart() {
        super.onStart()
        presenter.initFirebase()
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser?.displayName!!)
        presenter.initData()
        documentReference!!.get().addOnSuccessListener { doc ->
            if (doc!!.exists()) {
                presenter.initUserList(doc)
                planetDocument = firebaseFirestore.collection("Objects")
                        .document(presenter.getUserList()?.moveToObjectName!!)
                presenter.setObjectType()
            }
        }
        inventoryDocument = firebaseFirestore.collection("Inventory")
                .document(firebaseUser!!.displayName!!)
        inventoryDocument!!.addSnapshotListener(this) { documentSnapshot,
                                                        _ ->
            presenter.getUserIron(documentSnapshot)
        }
    }

    override fun setUserData(userList: User) {
        gate_coordinates.text = String.format(resources.getString(R.string.current_coordinate),
                userList.x, userList.y)
        gate_ship.text = userList.ship
        gate_hp.text = userList.hp.toString()
        gate_shield.text = userList.shield.toString()
        gate_cargo.text = userList.cargoCapacity.toString()
        gate_scanner_capacity.text = userList.scanner_capacity.toString()
        gate_fuel.text = userList.fuel.toString()
        gate_money.text = userList.money.toString()

        when (userList.moveToObjectType) {
            "planet" -> gate_action.text = resources.getString(R.string.land)
            "user" -> gate_action.text = resources.getString(R.string.fight)
            "fuel" -> gate_action.text = resources.getString(R.string.get_fuel)
            "debris" -> gate_action.text = resources.getString(R.string.mine)
            "meteorite_field" -> gate_action.text = resources.getString(R.string.mine)
        }
    }

    override fun setUserIron(userList: User, documentSnapshot: DocumentSnapshot?) {
        userList.resource_iron = documentSnapshot?.getLong("Iron")!! }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    override fun mineIron() {
        gate_action.setBackgroundColor(resources.getColor(R.color.grey))
        gate_action.isEnabled = false

        val buttonTimer = Timer()
        buttonTimer.schedule(object : TimerTask() {

            override fun run() {
                runOnUiThread {
                    gate_action.isEnabled = true
                    gate_action.setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
            }
        }, 5000)

        val random = Random().nextDouble() * 100
        if (random in 0.0..30.0) {
            Toast.makeText(this, "Fail :( Try again. Total: " + presenter.getUserList()?.resource_iron, Toast.LENGTH_LONG).show()
        }
        if (random in 30.0..40.0) {
            presenter.updateIron(1)
        }
        if (random in 40.0..60.0) {
            presenter.updateIron(2)
        }
        if (random in 60.0..80.0) {
            presenter.updateIron(3)
        }
        if (random in 80.0..90.0) {
            presenter.updateIron(5)
        }
        if (random in 90.0..95.0) {
            presenter.updateIron(10)
        }
        if (random in 95.0..97.5) {
            presenter.updateIron(50)
        }
        if (random in 97.5..100.0) {
            presenter.updateIron(100)
        }
    }

    override fun updateIron(counter: Int) {
        if (presenter.getUserList()?.resource_iron!! + counter <= presenter.getUserList()!!.cargoCapacity) {
            Toast.makeText(this, "You got " + counter + " iron. Total: " + (presenter.getUserList()!!.resource_iron + counter), Toast.LENGTH_LONG).show()
            inventoryDocument!!.update("Iron", presenter.getUserList()!!.resource_iron + counter)
        } else {
            Toast.makeText(this, "Your cargoCapacity is full!", Toast.LENGTH_LONG).show()
        }
    }

    private fun createDebris() {
        val debris = HashMap<String, Any>()
        val x = RandomUtils.getRandomCoordinate()
        val y = RandomUtils.getRandomCoordinate()
        debris["x"] = x
        debris["y"] = y
        debris["sumXY"] = x + y
        debris["type"] = "debris"
        debris["iron"] = RandomUtils.getRandomDebrisIron()

        val collectionReferencePlanet = firebaseFirestore?.collection("Objects")
        collectionReferencePlanet.document("Debris-" + RandomUtils.getRandomDebrisName(4))
                .set(debris)
    }

    private fun startTimer(finish: Long, debrisIsOver: Boolean) {

        countDownTimer = object : CountDownTimer(finish, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                remainedSecs = millisUntilFinished / 1000
                gate_action.text = resources.getString(R.string.mine_stop) + " " + remainedSecs / 60 + ":" + remainedSecs % 60
            }

            override fun onFinish() {

                inventoryDocument!!.update("Iron", presenter.getUserList()!!.resource_iron + maxTimeInMilliseconds / 1000)
                if (debrisIsOver) {
                    planetDocument!!.delete().addOnSuccessListener {
                        Toast.makeText(this@GateActivity, "Debris is empty and is not available anymore", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@GateActivity, MainOptionsActivity::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GateActivity).toBundle())
                        } else {
                            startActivity(intent)
                        }
                    }
                } else {

                    planetDocument = firebaseFirestore.collection("Objects")
                            .document(presenter.getUserList()!!.moveToObjectName!!)
                    planetDocument!!.update("iron", presenter.getObjectModel()!!.debrisIronAmount - maxTimeInMilliseconds / 1000)
                    Toast.makeText(this@GateActivity, "Mining is over. Cargo is full", Toast.LENGTH_SHORT).show()
                }
                gate_action.text = resources.getString(R.string.mine)
                cancel()
                countDownTimer = null
            }
        }.start()
    }

    override fun mineDebris() {

        debrisIsOver = false
        maxTimeInMilliseconds = (1000 * (presenter.getUserList()!!.cargoCapacity - presenter.getUserList()!!.resource_iron)).toLong()

        // If the iron amount on debris less than an amount that is needed to fill up fully the cargoCapacity

        if (presenter.getObjectModel()!!.debrisIronAmount < maxTimeInMilliseconds / 1000) {
            maxTimeInMilliseconds = (1000 * presenter.getObjectModel()!!.debrisIronAmount).toLong()
            debrisIsOver = true
            createDebris()
        }
        startTimer(maxTimeInMilliseconds, debrisIsOver)
    }

    fun onScan(view: View) {

        val intent = Intent(this, ScanResultActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    override fun setProgressBar() {
    }

    companion object {
        private val TAG = "myLogs"
        const val ENEMY_USERNAME = "ENEMY_USERNAME"
    }
}
