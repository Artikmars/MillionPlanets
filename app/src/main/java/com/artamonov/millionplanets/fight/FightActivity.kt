package com.artamonov.millionplanets.fight

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

import com.artamonov.millionplanets.model.ObjectModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference

import java.util.HashMap

import com.artamonov.millionplanets.gate.GateActivity
import com.artamonov.millionplanets.MainOptionsActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenter
import com.artamonov.millionplanets.fight.presenter.FightActivityPresenterImpl
import com.artamonov.millionplanets.gate.GateActivity.Companion.ENEMY_USERNAME
import com.artamonov.millionplanets.model.User
import kotlinx.android.synthetic.main.activity_fight.*
import kotlinx.android.synthetic.main.move.progressBar

class FightActivity : BaseActivity(), FightActivityView {
    private var parentLayout: View? = null
    private lateinit var objectModel: ObjectModel
    private var userDocument: DocumentReference? = null
    private var enemyDocument: DocumentReference? = null
    private var enemyUsername: String? = null

    lateinit var presenter: FightActivityPresenter<FightActivityView>

    private var snackbarOnClickListener: View.OnClickListener = View.OnClickListener {
        Toast.makeText(applicationContext, "Please, wait 10 seconds", Toast.LENGTH_LONG).show()
        val animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animation.duration = 10000
        animation.interpolator = DecelerateInterpolator()
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }
            override fun onAnimationEnd(animator: Animator) { }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animation.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight)
        presenter = FightActivityPresenterImpl(this)

        if (intent != null) {
            enemyUsername = intent.getStringExtra(ENEMY_USERNAME)
            enemy_label.text = enemyUsername
        }

        fight.setOnClickListener {
            presenter.calculateDamage()
        }
    }

    override fun onStart() {
        super.onStart()
        userDocument = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
        userDocument!!.addSnapshotListener(this) { doc, _ ->
            if (doc!!.exists()) {
                presenter.setUserList(doc)
            }
        }

        enemyDocument = firebaseFirestore.collection("Objects")
                .document(enemyUsername!!)
        enemyDocument!!.addSnapshotListener(this) { doc, _ ->
            if (doc!!.exists()) {
                presenter.setEnemyList(doc)
            }
        }
    }

    override fun setUserData(userList: User) {
        ship_you.text = userList.ship
        hp_you.text = userList.hp.toString()
        shield_you.text = userList.shield.toString()
        weapon_you.text = userList.damage?.size.toString() + "/3"
    }

    override fun setEnemyData(enemyList: User) {
        ship_enemy.text = enemyList.ship
        hp_enemy.text = enemyList.hp.toString()
        shield_enemy.text = enemyList.shield.toString()
        weapon_enemy.text = enemyList.damage?.size.toString() + "/3"
    }

    override fun showYouWonMessage() {
        fight_log.text = "YOU WON!"
        fight_log.setTextColor(resources.getColor(R.color.colorAccent))
        fight.isClickable = false
    }

    override fun showEnemyWonMessage() {
        fight_log.text = "YOU LOSE!"
        fight_log.setTextColor(resources.getColor(R.color.red))
        fight.isClickable = false
    }

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
                userDocument!!.update(movedPosition)
                startActivity(Intent(applicationContext, GateActivity::class.java))
            }
        }
    }

    companion object {

        private val TAG = "myLogs"
    }
}
