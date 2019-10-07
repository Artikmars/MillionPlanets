package com.artamonov.millionplanets.fight

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

import com.artamonov.millionplanets.adapter.ScanResultAdapter
import com.artamonov.millionplanets.model.ObjectModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference

import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.gate.GateActivity
import com.artamonov.millionplanets.MainOptionsActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenter
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenterImpl
import kotlinx.android.synthetic.main.move.*

class FightActivity : BaseActivity(), FightActivityView {

    private var parentLayout: View? = null
    private lateinit var objectModel: ObjectModel
    private var documentReference: DocumentReference? = null

    lateinit var presenter: FightActivityPresenter<FightActivityView>

    private var snackbarOnClickListener: View.OnClickListener = View.OnClickListener {
        Toast.makeText(applicationContext, "Please, wait 10 seconds", Toast.LENGTH_LONG).show()
        val animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animation.duration = 10000
        animation.interpolator = DecelerateInterpolator()
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }
            override fun onAnimationEnd(animator: Animator) { presenter.getFuel() }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animation.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.move)
        presenter = FightActivityPresenterImpl(this)
        parentLayout = findViewById(android.R.id.content)
        move_scan_result_list.layoutManager = LinearLayoutManager(this)

        val intent = intent
        val objectModelList = ArrayList<ObjectModel>()
        objectModel = ObjectModel()
        objectModel.type = intent.getStringExtra("objectType")
        objectModel.name = intent.getStringExtra("objectName")
        objectModel.distance = intent.getIntExtra("objectDistance", 0)
        objectModel.x = intent.getIntExtra("objectX", 0)
        objectModel.y = intent.getIntExtra("objectY", 0)
        objectModelList.add(objectModel)

        val scanResultAdapter = ScanResultAdapter(objectModelList)
        move_scan_result_list.adapter = scanResultAdapter
    }

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(this) { doc, e ->
            if (doc!!.exists()) {

                presenter.setUserList(doc)
                move_coordinates.text = String.format(resources.getString(R.string.current_coordinate),
                        presenter.userList.x, presenter.userList.y)
                move_ship.text = presenter.userList.ship
                move_hp.text = presenter.userList.hp.toString()
                move_shield.text = presenter.userList.shield.toString()
                move_cargo!!.text = presenter.userList.cargo.toString()
                move_scanner_capacity.text = presenter.userList.scanner_capacity.toString()
                move_fuel.text = presenter.userList.fuel.toString()
                move_money.text = presenter.userList.money.toString()
            }
        }
    }

    override fun buyFuel(fuel: Int, money: Int) {
        documentReference!!.update("fuel", fuel)
        documentReference!!.update("money", money) }

    override fun setProgressBar(state: Boolean) {
        progressBar.progress = 100
        progressBar.visibility = View.INVISIBLE
    }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    override fun setSnackbarError(errorMessage: Int) {
        parentLayout = findViewById(android.R.id.content)
        Snackbar.make(parentLayout!!, getString(R.string.run_out_of_fuel),
                Snackbar.LENGTH_LONG).setAction(R.string.call_tanker, snackbarOnClickListener).setDuration(4000).show() }

    fun onJump(view: View) {

        presenter.ifEnoughFuelToJump()

        if (presenter.userList.moveToObjectName != null) {
            val docRefForMovedObject = firebaseFirestore.collection("Objects")
                    .document(presenter.userList.moveToObjectName!!)
            docRefForMovedObject.get().addOnSuccessListener { documentSnapshot ->
                val x = documentSnapshot.getLong("x")!!.toInt()
                val y = documentSnapshot.getLong("y")!!.toInt()
                val movedPosition = HashMap<String, Any>()
                movedPosition["x"] = x
                movedPosition["y"] = y
                movedPosition["fuel"] = presenter.userList.fuel - presenter.userList.moveToObjectDistance
                movedPosition["sumXY"] = x + y
                documentReference!!.update(movedPosition)
                startActivity(Intent(applicationContext, GateActivity::class.java))
            }
        }
    }

    companion object {

        private val TAG = "myLogs"
    }
}
