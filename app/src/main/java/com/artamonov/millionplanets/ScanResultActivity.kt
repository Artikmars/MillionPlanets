package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.adapter.ScanResultAdapter
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.gate.GateActivity
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.gate.GateActivity.Companion.ENEMY_USERNAME
import com.artamonov.millionplanets.move.MoveActivity
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.fragment_scan_result.*
import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap
import kotlin.math.abs

class ScanResultActivity : BaseActivity(), ScanResultAdapter.ItemClickListener {

    internal var objectModelList: MutableList<ObjectModel>? = null
    internal var userList = User()
    private var tvPosition: TextView? = null
    private var tvShip: TextView? = null
    private var tvHp: TextView? = null
    private var tvShield: TextView? = null
    private var tvCargo: TextView? = null
    private var tv_ScannerCapacity: TextView? = null
    private var tvFuel: TextView? = null
    private var tvMoney: TextView? = null
    private var documentReference: DocumentReference? = null

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList.ship = doc.getString("ship")
                userList.x = doc.getLong("x")!!
                userList.y = doc.getLong("y")!!
                userList.sumXY = doc.getLong("sumXY")!!
                userList.hp = doc.getLong("hp")!!
                userList.cargoCapacity = doc.getLong("cargoCapacity")!!
                userList.fuel = doc.getLong("fuel")!!
                userList.scanner_capacity = doc.getLong("scanner_capacity")!!
                userList.shield = doc.getLong("shield")!!
                userList.money = doc.getLong("money")!!
                tvPosition!!.text = String.format(
                        resources.getString(R.string.current_coordinate),
                        userList.x,
                        userList.y)
                tvShip!!.text = userList.ship
                tvHp!!.text = userList.hp.toString()
                tvShield!!.text = userList.shield.toString()
                tvCargo!!.text = userList.cargoCapacity.toString()
                tv_ScannerCapacity!!.text = userList.scanner_capacity.toString()
                tvFuel!!.text = userList.fuel.toString()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_result)
        scan_result_list.layoutManager = LinearLayoutManager(this)

        /*  progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();*/

        tvPosition = findViewById(R.id.scan_coordinates)
        tvShip = findViewById(R.id.scan_ship)
        tvHp = findViewById(R.id.scan_hp)
        tvShield = findViewById(R.id.scan_shield)
        tvCargo = findViewById(R.id.scan_cargo)
        tv_ScannerCapacity = findViewById(R.id.scan_scanner_capacity)
        tvFuel = findViewById(R.id.scan_fuel)
        tvMoney = findViewById(R.id.scan_money)
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList.ship = doc.getString("ship")
                userList.x = doc.getLong("x")!!
                userList.y = doc.getLong("y")!!
                userList.sumXY = doc.getLong("sumXY")!!
                userList.hp = doc.getLong("hp")!!
                userList.cargoCapacity = doc.getLong("cargoCapacity")!!
                userList.fuel = doc.getLong("fuel")!!
                userList.scanner_capacity = doc.getLong("scanner_capacity")!!
                userList.shield = doc.getLong("shield")!!
                userList.money = doc.getLong("money")!!
                tvPosition!!.text = String.format(
                        resources.getString(R.string.current_coordinate),
                        userList.x,
                        userList.y)
                tvShip!!.text = userList.ship
                tvHp!!.text = userList.hp.toString()
                tvShield!!.text = userList.shield.toString()
                tvCargo!!.text = userList.cargoCapacity.toString()
                tv_ScannerCapacity!!.text = userList.scanner_capacity.toString()
                tvFuel!!.text = userList.fuel.toString()
                tvMoney!!.text = userList.money.toString()

                val objectRef = firebaseFirestore.collection("Objects")
                Log.i(
                        "myLogs",
                        "sumXY: + " + (userList.sumXY + userList.scanner_capacity))
                Log.i(
                        "myLogs",
                        "sumXY: - " + (userList.sumXY - userList.scanner_capacity))
                objectRef
                        .whereLessThanOrEqualTo(
                                "sumXY",
                                userList.sumXY + userList.scanner_capacity)
                        .whereGreaterThanOrEqualTo(
                                "sumXY",
                                userList.sumXY - userList.scanner_capacity)
                        .get()
                        .addOnSuccessListener { queryDocumentSnapshots ->
                            objectModelList = ArrayList()
                            for (document in queryDocumentSnapshots) {
                                Log.i(
                                        "myLogs",
                                        "document.getId: " + document.id)
                                val objectModel = ObjectModel()
                                objectModel.name = document.id
                                objectModel.type = document.getString("type")
                                objectModel.x = document.getLong("x")!!.toInt()
                                objectModel.y = document.getLong("y")!!.toInt()

                                //  Distinguish between (2;8) and (3;7)
                                if (document.getLong("sumXY")!! == userList.sumXY) {
                                    objectModel.distance = Math.abs(
                                            document.getLong("x")!!.toInt() - userList
                                                    .x.toInt())
                                } else {
                                    objectModel.distance = abs(
                                            document.getLong(
                                                    "sumXY")!!
                                                    .toInt() - userList
                                                    .sumXY.toInt())
                                }

                                if (objectModel.distance <= userList.scanner_capacity) {
                                    objectModelList?.add(objectModel)
                                }
                            }

                            // Excluding the user itself in the search
                            for (i in objectModelList!!.indices) {
                                if (objectModelList!![i]
                                                .name == firebaseUser!!
                                                .displayName) {
                                    objectModelList!!.removeAt(i)
                                    break
                                }
                            }

                            objectModelList?.sortWith(Comparator { objectModel, t1 ->
                                objectModel
                                        .distance!! - t1.distance!!
                            })

                            val scanResultAdapter = ScanResultAdapter(
                                    objectModelList,
                                    this@ScanResultActivity)
                            scan_result_list.adapter = scanResultAdapter
                            scanResultAdapter.notifyDataSetChanged()
                        }
            }
        }
    }

    /* View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getApplicationContext(), "Please, wait 10 seconds", Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    documentReference.update("fuel", 20);
                }
            }, 10000);
        }
    };*/

    fun onGoBackToMainOptions(view: View) {
        finish()
    }

    /* private int getFuelRate(int x1, int x2, int y1, int y2, long current_fuel) {

    }*/

    override fun onItemClick(pos: Int) {

        val moveToObject = HashMap<String, Any>()
        moveToObject["locationName"] = objectModelList!![pos].name
        moveToObject["locationType"] = objectModelList!![pos].type
        moveToObject["moveToObjectDistance"] = objectModelList!![pos].distance!!
        //  moveToObject.put("fuel", userList.getFuel() - objectModelList.get(pos).getDistance());
        documentReference!!.update(moveToObject)

        if (objectModelList!![pos].distance == 0) {
            when (objectModelList!![pos].type) {
                "planet" -> {
                    val intent = Intent(this, PlanetActivity::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(
                                intent,
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                        finish()
                    } else {
                        startActivity(intent)
                        finish()
                    }
                }
                else -> {
                    val intent2 = Intent(this, GateActivity::class.java)
                    intent2.putExtra(ENEMY_USERNAME, objectModelList!![pos].name)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(
                                intent2,
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                    } else {
                        startActivity(intent2)
                    }
                }
            }
        } else {
            val intent = Intent(this, MoveActivity::class.java)
            // Show only the recycler view item which was clicked
            val objectModel = objectModelList!![pos]
            intent.putExtra("objectType", objectModel.type)
            intent.putExtra("objectName", objectModel.name)
            intent.putExtra("objectDistance", objectModel.distance)
            intent.putExtra("objectX", objectModel.x)
            intent.putExtra("objectY", objectModel.y)
            Log.i("myLogs", "onItemClick in ScanResult: x: " + objectModel.x!!)
            Log.i("myLogs", "onItemClick in ScanResult: y: " + objectModel.y!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@ScanResultActivity,
                        scan_result_list,
                        ViewCompat.getTransitionName(scan_result_list)!!)
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
        }
    }
}
