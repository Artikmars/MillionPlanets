package com.artamonov.millionplanets.gate

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.artamonov.millionplanets.R
import java.util.Timer
import java.util.TimerTask
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityGateBinding
import com.artamonov.millionplanets.gate.viewmodel.GateViewModel
import com.artamonov.millionplanets.fight.FightActivity
import com.artamonov.millionplanets.gate.models.FetchGateStatus
import com.artamonov.millionplanets.gate.models.GateAction
import com.artamonov.millionplanets.gate.models.GateEvent
import com.artamonov.millionplanets.gate.models.GateViewState
import com.artamonov.millionplanets.model.SpaceObjectType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.showLongSnackbar
import com.artamonov.millionplanets.utils.showSnackbarError
import com.artamonov.millionplanets.utils.startPlanetActivity
import com.artamonov.millionplanets.utils.startMainOptionsActivity
import com.artamonov.millionplanets.utils.startScanResultActivity
import com.google.firebase.firestore.DocumentSnapshot

class GateActivity : BaseActivity(), GateActivityView {

    private var maxTimeInMilliseconds: Long = 0
    private var debrisIsOver: Boolean = false
    private var enemyUsername: String? = null

    private val viewModel: GateViewModel by viewModels()

    private lateinit var binding: ActivityGateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent != null) { enemyUsername = intent.getStringExtra(ENEMY_USERNAME) }
        binding.gateAction.setOnClickListener { viewModel.obtainEvent(GateEvent.SetObjectType) }
        binding.gateScan.setOnClickListener { scan() }

        viewModel.viewEffects().observe(this, { bindViewAction(it) })
        viewModel.viewStates().observe(this, { bindViewState(it) })
    }

    private fun bindViewState(viewState: GateViewState) {
        when (viewState.fetchStatus) {
            is FetchGateStatus.DefaultState -> {
                setUserData(viewState.user)
            }
            is FetchGateStatus.MineIron -> {
                mineIron()
            }
            is FetchGateStatus.MineDebris -> {
                mineDebris()
            }
            is FetchGateStatus.SetFightType -> {
                setFightType()
            }
            is FetchGateStatus.OpenPlanetActivity -> {
                openPlanetActivity()
            }
            is FetchGateStatus.ShowNotEnoughMoneyToBuyFuelWarning -> {
                showNotEnoughMoneyToBuyFuelWarning()
            }
            is FetchGateStatus.DebrisIsRemoved -> {
                debrisIsRemoved()
            }
            is FetchGateStatus.ShowCapacityError -> {
                showCapacityError()
            }
        }
    }

    private fun bindViewAction(it: GateAction?) {
        when (it) {
            is GateAction.ShowMineFailedSnackbar -> {
                showSnackbarError(getString(R.string.gate_mine_failed,
                        viewModel.viewStates().value?.user?.resource_iron))
            }
            is GateAction.OpenFightActivity -> {
                openFightActivity(it.enemyUsername)
            }
            is GateAction.SetUserIron -> {
                setUserIron(it.userList, it.documentSnapshot)
            }
            is GateAction.ShowUpdateIronToast -> {
                showUpdateIronToast(it.counter, it.totalAmount)
            }
        }
    }

    private fun showNotEnoughMoneyToBuyFuelWarning() {
        showSnackbarError(getString(R.string.gate_not_enough_money_to_buy_fuel))
    }

    private fun openPlanetActivity() {
        startPlanetActivity()
        finish()
    }

    private fun openFightActivity(enemyUsername: String?) {
        val intent = Intent(this, FightActivity::class.java)
        intent.putExtra(ENEMY_USERNAME, enemyUsername)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
        finish()
    }

    private fun setFightType() {
        binding.gateAction.text = resources.getString(R.string.fight_with,
                viewModel.viewStates().value?.user?.locationName)
    }

    private fun setUserData(userList: User) {
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
            SpaceObjectType.PLANET -> binding.gateAction.text = resources.getString(R.string.land)
            SpaceObjectType.USER -> binding.gateAction.text = resources.getString(R.string.fight)
            SpaceObjectType.FUEL -> binding.gateAction.text = resources.getString(R.string.get_fuel)
            SpaceObjectType.DEBRIS -> binding.gateAction.text = resources.getString(R.string.mine)
            SpaceObjectType.METEORITE_FIELD -> binding.gateAction.text = resources.getString(R.string.mine)
        }
    }

    override fun setUserIron(userList: User, documentSnapshot: DocumentSnapshot?) {
    }

    private fun mineIron() {
        binding.gateAction.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
        binding.gateAction.isEnabled = false

        val buttonTimer = Timer()
        buttonTimer.schedule(object : TimerTask() {

            override fun run() {
                runOnUiThread {
                    binding.gateAction.isEnabled = true
                    binding.gateAction.setBackgroundColor(ContextCompat.getColor(this@GateActivity, R.color.colorAccent))
                }
            }
        }, 5000)

        viewModel.obtainEvent(GateEvent.UpdateIron)
    }

    private fun mineDebris() {
        mineIron()
        viewModel.obtainEvent(GateEvent.UpdateIron)

        // If the iron amount on debris less than an amount that is needed to fill up fully the cargoCapacity

        if (viewModel.viewStates().value?.spaceObject?.debrisIronAmount!! < maxTimeInMilliseconds / 1000) {
            maxTimeInMilliseconds = (1000 * viewModel.viewStates().value?.spaceObject!!.debrisIronAmount)
            debrisIsOver = true
        }
    }

    override fun showUpdateIronToast(counter: Int, totalAmount: Long) {
        showLongSnackbar(getString(R.string.gate_you_got_item, counter, totalAmount))
    }

    private fun showCapacityError() {
        showSnackbarError(getString(R.string.gate_cargo_capacity_is_full))
    }

    private fun debrisIsRemoved() {
        showSnackbarError(getString(R.string.gate_debris_is_empty))
        startMainOptionsActivity()
        finish()
    }

    private fun scan() {
        startScanResultActivity()
        finish()
    }

    companion object {
        const val ENEMY_USERNAME = "ENEMY_USERNAME"
    }
}
