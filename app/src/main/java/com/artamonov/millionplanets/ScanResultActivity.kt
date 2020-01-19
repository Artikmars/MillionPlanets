package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
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
import kotlinx.android.synthetic.main.fragment_scan_result.scan_result_list
import kotlinx.android.synthetic.main.scan_result.*
import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap
import kotlin.math.abs

class ScanResultActivity : BaseActivity(), ScanResultAdapter.ItemClickListener {

    internal var objectModelList: MutableList<ObjectModel>? = null
    internal var userList = User()
    private var documentReference: DocumentReference? = null

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList = doc.toObject(User::class.java)!!
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

        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList = doc.toObject(User::class.java)!!

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

                val objectRef = firebaseFirestore.collection("Objects")

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

                                val objectModel = document.toObject(ObjectModel::class.java)
                                objectModel.name = document.id

                                //  Distinguish between (2;8) and (3;7)
                                if (document.getLong("sumXY")!! == userList.sumXY) {
                                    objectModel.distance = abs(
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