package com.artamonov.millionplanets.scanresult

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.PlanetActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.adapter.ScanResultAdapter
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.gate.GateActivity
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.gate.GateActivity.Companion.ENEMY_USERNAME
import com.artamonov.millionplanets.move.MoveActivity
import kotlinx.android.synthetic.main.scan_result.*

class ScanResultActivity : BaseActivity(), ScanResultAdapter.ItemClickListener {

    internal var objectModelList: MutableList<ObjectModel>? = null
    internal var userList = User()
    private var scanResultAdapter: ScanResultAdapter? = null
    private var scanResultViewModel: ScanResultViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_result)
        scan_result_list.layoutManager = LinearLayoutManager(this)

        scanResultViewModel = ViewModelProviders.of(this).get(ScanResultViewModel::class.java)
        scanResultViewModel?.getUser()?.observe(this, Observer { user ->
            userList = user
            scan_coordinates.text = String.format(
                    resources.getString(R.string.current_coordinate),
                    userList.x,
                    userList.y)
            scan_ship.text = userList.ship
            scan_hp.text = userList.hp.toString()
            scan_shield.text = userList.shield.toString()
            scan_cargo.text = userList.cargoCapacity.toString()
            scan_scanner_capacity.text = userList.scanner_capacity.toString()
            scan_fuel.text = userList.fuel.toString()
            scan_money.text = userList.money.toString()
        })

        scanResultViewModel?.getObject()?.observe(this, Observer { objectList ->
            objectModelList = objectList
            scanResultAdapter = ScanResultAdapter(
                    objectList,
                    this@ScanResultActivity)
            scan_result_list.adapter = scanResultAdapter
            scanResultAdapter?.notifyDataSetChanged()
        })

        scanResultViewModel?.getOpenPlanetLiveData()?.observe(this, Observer {
            val intent = Intent(this, PlanetActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
            finish()
        })

        scanResultViewModel?.getOpenGateLiveData()?.observe(this, Observer { pos ->
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

        scanResultViewModel?.getOpenMoveLiveData()?.observe(this, Observer { pos ->
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
                        scan_result_list,
                        ViewCompat.getTransitionName(scan_result_list)!!)
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
        })

        fun onGoBackToMainOptions(view: View) {
            finish()
        }
    }

    override fun onItemClick(pos: Int) {
        scanResultViewModel?.onObjectClicked(pos)
    }
}