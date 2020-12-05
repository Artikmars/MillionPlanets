package com.artamonov.millionplanets.move

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

import com.artamonov.millionplanets.adapter.ScanResultAdapter
import com.artamonov.millionplanets.model.SpaceObject
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference

import java.util.ArrayList
import java.util.HashMap

import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.gate.GateActivity
import com.artamonov.millionplanets.MainOptionsActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityMoveBinding
import com.artamonov.millionplanets.move.presenter.MoveActivityPresenter
import com.artamonov.millionplanets.move.presenter.MoveActivityPresenterImpl

class MoveActivity : BaseActivity(), MoveActivityView {
    private lateinit var objectModel: SpaceObject
    private var documentReference: DocumentReference? = null

    lateinit var presenter: MoveActivityPresenter<MoveActivityView>
    lateinit var binding: ActivityMoveBinding

    private var snackbarOnClickListener: View.OnClickListener = View.OnClickListener {
        Toast.makeText(applicationContext, "Please, wait 10 seconds", Toast.LENGTH_LONG).show()
        val animation = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100)
        animation.duration = 10000
        animation.interpolator = DecelerateInterpolator()
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }
            override fun onAnimationEnd(animator: Animator) { presenter.fillFuel()
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animation.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = MoveActivityPresenterImpl(this)
        binding.moveScanResultList.layoutManager = LinearLayoutManager(this)

        val intent = intent
        val objectModelList = ArrayList<SpaceObject>()
        objectModel = SpaceObject()
        objectModel.type = intent.getStringExtra("objectType")
        objectModel.name = intent.getStringExtra("objectName")
        objectModel.distance = intent.getLongExtra("objectDistance", 0)
        objectModel.x = intent.getLongExtra("objectX", 0)
        objectModel.y = intent.getLongExtra("objectY", 0)
        objectModelList.add(objectModel)

        val scanResultAdapter = ScanResultAdapter(objectModelList)
        binding.moveScanResultList.adapter = scanResultAdapter

        binding.moveJump.setOnClickListener {
            presenter.ifEnoughFuelToJump()

            if (presenter.getUserList().moveToLocationName != null) {
                val docRefForMovedObject = firebaseFirestore.collection("Objects")
                        .document(presenter.getUserList().moveToLocationName!!)
                docRefForMovedObject.get().addOnSuccessListener { documentSnapshot ->
                    val x = documentSnapshot.getLong("x")!!.toInt()
                    val y = documentSnapshot.getLong("y")!!.toInt()
                    val movedPosition = HashMap<String, Any>()
                    movedPosition["x"] = x
                    movedPosition["y"] = y
                    movedPosition["fuel"] = presenter.getUserList().fuel!! - presenter.getUserList().moveToObjectDistance!!
                    movedPosition["sumXY"] = x + y
                    movedPosition["locationName"] = presenter.getUserList().moveToLocationName!!
                    movedPosition["locationType"] = presenter.getUserList().moveToLocationType!!
                    documentReference!!.update(movedPosition)
                    startActivity(Intent(applicationContext, GateActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(this) { doc, e ->
            if (doc!!.exists()) {

                presenter.setUserList(doc)
                binding.moveCoordinates.text = String.format(resources.getString(R.string.current_coordinate),
                        presenter.getUserList().x, presenter.getUserList().y)
                binding.moveShip.text = presenter.getUserList().ship
                binding.moveHp.text = presenter.getUserList().hp.toString()
                binding.moveShield.text = presenter.getUserList().shield.toString()
                binding.moveCargo.text = presenter.getUserList().cargoCapacity.toString()
                binding.moveScannerCapacity.text = presenter.getUserList().scanner_capacity.toString()
                binding.moveFuel.text = presenter.getUserList().fuel.toString()
                binding.moveMoney.text = presenter.getUserList().money?.toString()
            }
        }
    }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    override fun buyFuel(fuel: Long, money: Long) {
        documentReference!!.update("fuel", fuel)
        documentReference!!.update("money", money) }

    override fun setProgressBar(state: Boolean) {
        binding.progressBar.progress = 100
        binding.progressBar.visibility = View.INVISIBLE
    }

    override fun setSnackbarError(errorMessage: Int) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.run_out_of_fuel),
                Snackbar.LENGTH_LONG).setAction(R.string.call_tanker, snackbarOnClickListener).setDuration(4000).show() }
}
