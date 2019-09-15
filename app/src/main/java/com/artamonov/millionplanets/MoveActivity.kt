package com.artamonov.millionplanets

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.Toast

import com.artamonov.millionplanets.adapter.ScanResultAdapter
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

import java.util.ArrayList
import java.util.HashMap

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.move.*

class MoveActivity : AppCompatActivity() {
    lateinit var firebaseFirestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    internal var userList = User()
    private var firebaseUser: FirebaseUser? = null
    private var parentLayout: View? = null
    private var objectModel: ObjectModel? = null
    private var documentReference: DocumentReference? = null
    private var progressBar: ProgressBar? = null
    private var snackbarOnClickListener: View.OnClickListener = View.OnClickListener {
        Toast.makeText(applicationContext, "Please, wait 10 seconds", Toast.LENGTH_LONG).show()
        val animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animation.duration = 10000
        animation.interpolator = DecelerateInterpolator()
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                val fuelToFill = Utils.getShipFuelInfo(userList.ship) - userList.fuel
                val price = fuelToFill * 1000
                if (userList.money >= price) {
                    documentReference!!.update("fuel", Utils.getShipFuelInfo(userList.ship))
                    documentReference!!.update("money", userList.money - price)
                    progressBar!!.progress = 100
                    progressBar!!.visibility = View.INVISIBLE
                }
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animation.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        parentLayout = findViewById(android.R.id.content)
        progressBar = findViewById(R.id.progressBar)
        move_scan_result_list.layoutManager = LinearLayoutManager(this)

        val intent = intent
        val objectModelList = ArrayList<ObjectModel>()
        objectModel = ObjectModel()
        objectModel!!.type = intent.getStringExtra("objectType")
        objectModel!!.name = intent.getStringExtra("objectName")
        objectModel!!.distance = intent.getIntExtra("objectDistance", 0)
        objectModel!!.x = intent.getIntExtra("objectX", 0)
        objectModel!!.y = intent.getIntExtra("objectY", 0)
        objectModelList.add(objectModel!!)

        val scanResultAdapter = ScanResultAdapter(objectModelList)
        move_scan_result_list.adapter = scanResultAdapter
    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(this) { doc, e ->
            if (doc!!.exists()) {
                userList.ship = doc.getString("ship")
                userList.x = doc.getLong("x")!!.toInt()
                userList.y = doc.getLong("y")!!.toInt()
                userList.sumXY = doc.getLong("sumXY")!!.toInt()
                userList.hp = doc.getLong("hp")!!.toInt()
                userList.cargo = doc.getLong("cargo")!!.toInt()
                userList.fuel = doc.getLong("fuel")!!.toInt()
                userList.scanner_capacity = doc.getLong("scanner_capacity")!!.toInt()
                userList.shield = doc.getLong("shield")!!.toInt()
                userList.money = doc.getLong("money")!!.toInt()
                userList.moveToObjectName = doc.getString("moveToObjectName")
                userList.moveToObjectDistance = doc.getLong("moveToObjectDistance")!!.toInt()

                move_coordinates.text = String.format(resources.getString(R.string.current_coordinate),
                        userList.x, userList.y)
                move_ship.text = userList.ship
                move_hp.text = userList.hp.toString()
                move_shield.text = userList.shield.toString()
                move_cargo!!.text = userList.cargo.toString()
                move_scanner_capacity.text = userList.scanner_capacity.toString()
                move_fuel.text = userList.fuel.toString()
                move_money.text = userList.money.toString()
            }
        }
    }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    fun onJump(view: View) {

        parentLayout = findViewById(android.R.id.content)
        Log.i("myLogs", "onItemClick: userList.getFuel()" + userList.fuel + ", objectModelList.get(pos).getDistance(): " +
                userList.moveToObjectDistance)
        if (userList.fuel == 0) {
            Snackbar.make(parentLayout!!, "You are run out of fuel! Please, call the tanker. ",
                    Snackbar.LENGTH_LONG).setAction(R.string.call_tanker, snackbarOnClickListener).setDuration(4000).show()
            return
        }
        if (userList.fuel - userList.moveToObjectDistance < 0) {
            Snackbar.make(parentLayout!!, "Not enough fuel to get to the destination. Please, call the tanker.",
                    Snackbar.LENGTH_LONG).setAction(R.string.call_tanker, snackbarOnClickListener).setDuration(4000).show()
            return
        }

        val docRefForMovedObject = firebaseFirestore.collection("Objects")
                .document(userList.moveToObjectName)
        docRefForMovedObject.get().addOnSuccessListener { documentSnapshot ->
            val x = documentSnapshot.getLong("x")!!.toInt()
            val y = documentSnapshot.getLong("y")!!.toInt()
            val movedPosition = HashMap<String, Any>()
            movedPosition["x"] = x
            movedPosition["y"] = y
            movedPosition["fuel"] = userList.fuel - userList.moveToObjectDistance
            movedPosition["sumXY"] = x + y
            documentReference!!.update(movedPosition)
            startActivity(Intent(applicationContext, GateActivity::class.java))
        }
    }

    companion object {

        private val TAG = "myLogs"
    }
}
