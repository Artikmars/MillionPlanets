package com.artamonov.millionplanets.gate

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.artamonov.millionplanets.MainOptionsActivity
import com.artamonov.millionplanets.PlanetActivity
import com.artamonov.millionplanets.R
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.GateActivityBinding
import com.artamonov.millionplanets.gate.presenter.GateActivityPresenter
import com.artamonov.millionplanets.gate.presenter.GateActivityPresenterImpl
import com.artamonov.millionplanets.scanresult.ScanResultActivity
import com.artamonov.millionplanets.fight.FightActivity
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot

class GateActivity : BaseActivity(), GateActivityView {

    private var maxTimeInMilliseconds: Long = 0
    private var debrisIsOver: Boolean = false
    private var enemyUsername: String? = null

    lateinit var presenter: GateActivityPresenter<GateActivityView>
    private lateinit var binding: GateActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = GateActivityPresenterImpl(this)

        if (intent != null) { enemyUsername = intent.getStringExtra(ENEMY_USERNAME) }
        binding.gateAction.setOnClickListener { presenter.setObjectType() }
    }

    override fun showNotEnoughMoneyToBuyFuelWarning() {
        showSnackbarError(getString(R.string.gate_not_enough_money_to_buy_fuel))
    }

    override fun openPlanetActivity() {
        val intent = Intent(this, PlanetActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
        finish()
    }

    override fun openFightActivity(enemyUsername: String?) {
        val intent = Intent(this, FightActivity::class.java)
        intent.putExtra(ENEMY_USERNAME, enemyUsername)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
        finish()
    }

    override fun setFightType() {
        binding.gateAction.text = resources.getString(R.string.fight_with, presenter.getUserList()?.locationName)
    }

    override fun onStart() {
        super.onStart()
        presenter.initFirebase()
//        documentReference = firebaseFirestore.collection("Objects")
//                .document(firebaseUser?.displayName!!)
        presenter.initData()

//        documentReference!!.get().addOnSuccessListener { doc ->
//            if (doc!!.exists()) {
//                presenter.initUserList(doc)
//                planetDocument = firebaseFirestore.collection("Objects")
//                        .document(presenter.getUserList()?.locationName!!)
//            }
//        }
//

//        inventoryDocument = firebaseFirestore.collection("Inventory")
//                .document(firebaseUser!!.displayName!!)
//        inventoryDocument!!.addSnapshotListener(this) { documentSnapshot,
//                                                        _ ->
//            presenter.getUserIron(documentSnapshot)
//        }
    }

    override fun setUserData(userList: User) {
        binding.gateCoordinates.text = String.format(resources.getString(R.string.current_coordinate),
                userList.x, userList.y)
        binding.gateShip.text = userList.ship
        binding.gateHp.text = userList.hp.toString()
        binding.gateShield.text = userList.shield.toString()
        binding.gateCargo.text = userList.cargoCapacity.toString()
        binding.gateScannerCapacity.text = userList.scanner_capacity.toString()
        binding.gateFuel.text = userList.fuel.toString()
        binding.gateMoney.text = userList.money.toString()

        when (userList.locationType) {
            "planet" -> binding.gateAction.text = resources.getString(R.string.land)
            "user" -> binding.gateAction.text = resources.getString(R.string.fight)
            "fuel" -> binding.gateAction.text = resources.getString(R.string.get_fuel)
            "debris" -> binding.gateAction.text = resources.getString(R.string.mine)
            "meteorite_field" -> binding.gateAction.text = resources.getString(R.string.mine)
        }
    }

    override fun setUserIron(userList: User, documentSnapshot: DocumentSnapshot?) {
    }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    override fun mineIron() {
        binding.gateAction.setBackgroundColor(resources.getColor(R.color.grey))
        binding.gateAction.isEnabled = false

        val buttonTimer = Timer()
        buttonTimer.schedule(object : TimerTask() {

            override fun run() {
                runOnUiThread {
                    binding.gateAction.isEnabled = true
                    binding.gateAction.setBackgroundColor(resources.getColor(R.color.colorAccent))
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

    override fun showUpdateIronToast(counter: Int, totalAmount: Long) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.gate_you_got_item,
        counter, totalAmount), Snackbar.LENGTH_LONG).show()
    }

    override fun showCapacityError() {
        showSnackbarError(getString(R.string.gate_cargo_capacity_is_full))
    }

    override fun debrisIsRemoved() {
        showSnackbarError(getString(R.string.gate_debris_is_empty))
        val intent = Intent(this, MainOptionsActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
        finish()
    }

//    private fun startTimer(finish: Long, debrisIsOver: Boolean) {
//
//        countDownTimer = object : CountDownTimer(finish, 1000) {
//
//            override fun onTick(millisUntilFinished: Long) {
//                remainedSecs = millisUntilFinished / 1000
//                gate_action.text = resources.getString(R.string.mine_stop) + " " + remainedSecs / 60 + ":" + remainedSecs % 60
//            }
//
//            override fun onFinish() {
//
//             //   inventoryDocument!!.update("Iron", presenter.getUserList()!!.resource_iron + maxTimeInMilliseconds / 1000)
//                if (debrisIsOver) {
//                    planetDocument!!.delete().addOnSuccessListener {
//                        Toast.makeText(this@GateActivity, "Debris is empty and is not available anymore", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this@GateActivity, MainOptionsActivity::class.java)
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GateActivity).toBundle())
//                        } else {
//                            startActivity(intent)
//                        }
//                    }
//                } else {
//
//                    planetDocument = firebaseFirestore.collection("Objects")
//                            .document(presenter.getUserList()!!.locationName!!)
//                    planetDocument!!.update("iron", presenter.getObjectModel()!!.debrisIronAmount - maxTimeInMilliseconds / 1000)
//                    Toast.makeText(this@GateActivity, "Mining is over. Cargo is full", Toast.LENGTH_SHORT).show()
//                }
//                gate_action.text = resources.getString(R.string.mine)
//                cancel()
//                countDownTimer = null
//            }
//        }.start()
//    }

    override fun mineDebris() {

        binding.gateAction.setBackgroundColor(resources.getColor(R.color.grey))
        binding.gateAction.isEnabled = false

        val buttonTimer = Timer()
        buttonTimer.schedule(object : TimerTask() {

            override fun run() {
                runOnUiThread {
                    binding.gateAction.isEnabled = true
                    binding.gateAction.setBackgroundColor(resources.getColor(R.color.colorAccent))
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
//
//        debrisIsOver = false
//        maxTimeInMilliseconds = (1000 * (presenter.getUserList()!!.cargoCapacity - presenter.getUserList()!!.resource_iron))

        // If the iron amount on debris less than an amount that is needed to fill up fully the cargoCapacity

        if (presenter.getObjectModel()!!.debrisIronAmount < maxTimeInMilliseconds / 1000) {
            maxTimeInMilliseconds = (1000 * presenter.getObjectModel()!!.debrisIronAmount).toLong()
            debrisIsOver = true
            // createDebris()
        }
        // startTimer(maxTimeInMilliseconds, debrisIsOver)
    }

    fun onScan(view: View) {

        val intent = Intent(this, ScanResultActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
        finish()
    }

    override fun setProgressBar() {
    }

    companion object {
        const val ENEMY_USERNAME = "ENEMY_USERNAME"
    }
}
