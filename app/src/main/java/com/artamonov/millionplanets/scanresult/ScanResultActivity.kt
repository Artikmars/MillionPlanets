package com.artamonov.millionplanets.scanresult

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.PlanetActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.adapter.ScanResultAdapter
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityScanResultBinding
import com.artamonov.millionplanets.gate.GateActivity
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.gate.GateActivity.Companion.ENEMY_USERNAME
import com.artamonov.millionplanets.move.MoveActivity

class ScanResultActivity : BaseActivity(), ScanResultAdapter.ItemClickListener {

    private var objectModelList: MutableList<SpaceObject>? = null
    internal var userList = User()
    private var scanResultAdapter: ScanResultAdapter? = null
    private val scanResultViewModel: ScanResultViewModel by viewModels()

    lateinit var binding: ActivityScanResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.scanResultList.layoutManager = LinearLayoutManager(this)

        scanResultViewModel?.getUser()?.observe(this, { user ->
            userList = user
            binding.scanCoordinates.text = String.format(
                    resources.getString(R.string.current_coordinate),
                    userList.x,
                    userList.y)
            binding.scanShip.text = userList.ship
            binding.scanHp.text = userList.hp.toString()
            binding.scanShield.text = userList.shield.toString()
            binding.scanCargo.text = userList.cargoCapacity.toString()
            binding.scanScannerCapacity.text = userList.scanner_capacity.toString()
            binding.scanFuel.text = userList.fuel.toString()
            binding.scanMoney.text = userList.money.toString()
        })

        scanResultViewModel?.getObject()?.observe(this, { objectList ->
            objectModelList = objectList
            scanResultAdapter = ScanResultAdapter(
                    objectList,
                    this@ScanResultActivity)
            binding.scanResultList.adapter = scanResultAdapter
            scanResultAdapter?.notifyDataSetChanged()
        })

        scanResultViewModel?.getOpenPlanetLiveData()?.observe(this, {
            val intent = Intent(this, PlanetActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
            finish()
        })

        scanResultViewModel?.getOpenGateLiveData()?.observe(this, { pos ->
            val intent2 = Intent(this, GateActivity::class.java)
            intent2.putExtra(ENEMY_USERNAME, objectModelList!![pos].name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(
                        intent2,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent2)
            }
        })

        scanResultViewModel?.getOpenMoveLiveData()?.observe(this, { pos ->
            val intent = Intent(this, MoveActivity::class.java)
            // Show only the recycler view item which was clicked
            val objectModel = objectModelList!![pos]
            intent.putExtra("objectType", objectModel.type)
            intent.putExtra("objectName", objectModel.name)
            intent.putExtra("objectDistance", objectModel.distance)
            intent.putExtra("objectX", objectModel.x)
            intent.putExtra("objectY", objectModel.y)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@ScanResultActivity,
                        binding.scanResultList,
                        ViewCompat.getTransitionName(binding.scanResultList)!!)
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
        })

        binding.backToMenu.setOnClickListener {
            finish()
        }
    }

    override fun onItemClick(pos: Int) {
        scanResultViewModel?.onObjectClicked(pos)
    }
}