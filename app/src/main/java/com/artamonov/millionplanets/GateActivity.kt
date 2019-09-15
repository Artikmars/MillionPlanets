package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.RandomUtils
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Transaction

import java.util.HashMap
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.gate.*

class GateActivity : AppCompatActivity() {

    internal var userList = User()
    internal var firebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseUser: FirebaseUser? = null
    private var documentReference: DocumentReference? = null
    private var documentReferenceInventory: DocumentReference? = null
    private var documentReferencePlanet: DocumentReference? = null
    private val objectModel = ObjectModel()
    private var maxTimeInMilliseconds: Long = 0
    private var debrisIsOver: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var remainedSecs: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gate)
        firebaseUser = FirebaseAuth.getInstance().currentUser

    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser!!.displayName!!)
        firebaseFirestore.runTransaction { transaction ->
            val documentSnapshot = transaction.get(documentReference!!)
            userList.moveToObjectName = documentSnapshot.getString("moveToObjectName")
            documentReferencePlanet = firebaseFirestore.collection("Objects")
                    .document(userList.moveToObjectName)
            val documentSnapshot2 = transaction.get(documentReferencePlanet!!)
            objectModel.debrisIronAmount = documentSnapshot2.getLong("iron")!!.toInt()
            transaction.update(documentReference!!, "moveToObjectName", userList.moveToObjectName)
            transaction.update(documentReferencePlanet!!, "iron", objectModel.debrisIronAmount)
            null
        }
        documentReference!!.addSnapshotListener(this) { doc, e ->
            if (doc!!.exists()) {
                userList.moveToObjectName = doc.getString("moveToObjectName")
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
                userList.moveToObjectType = doc.getString("moveToObjectType")



                gate_coordinates.setText(String.format(resources.getString(R.string.current_coordinate),
                        userList.x, userList.y))
                gate_ship.text = userList.ship
                gate_hp.text = userList.hp.toString()
                gate_shield.text = userList.shield.toString()
                gate_cargo.text = userList.cargo.toString()
                gate_scanner_capacity.text = userList.scanner_capacity.toString()
                gate_fuel.text = userList.fuel.toString()
                gate_money.text = userList.money.toString()


                when (userList.moveToObjectType) {
                    "planet" -> gate_action.text = resources.getString(R.string.land)
                    "user" -> gate_action.text = resources.getString(R.string.fight)
                    "fuel" -> gate_action.text = resources.getString(R.string.get_fuel)
                    "debris" -> gate_action.text = resources.getString(R.string.mine)
                    "meteorite_field" -> gate_action.text = resources.getString(R.string.mine)
                }
            }
        }

        documentReferenceInventory = firebaseFirestore.collection("Inventory")
                .document(firebaseUser!!.displayName!!)
        documentReferenceInventory!!.addSnapshotListener(this) { documentSnapshot, e -> userList.resource_iron = documentSnapshot!!.getLong("Iron")!!.toInt() }
    }

    fun onGoBackToMainOptions(view: View) {
        startActivity(Intent(applicationContext, MainOptionsActivity::class.java))
    }

    private fun mineIron() {
        gate_action.setBackgroundColor(resources.getColor(R.color.grey))
        gate_action.isEnabled = false

        val buttonTimer = Timer()
        buttonTimer.schedule(object : TimerTask() {

            override fun run() {
                runOnUiThread {
                    gate_action.isEnabled = true
                    gate_action.setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
            }
        }, 5000)

        /* float i = new Random().nextFloat();
        if (i > 0 && i < 0.3) {
            Toast.makeText(this, "You got 1 iron. ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Fail :( Try again. ", Toast.LENGTH_SHORT).show();
        }*/

        val random = Random().nextDouble() * 100
        if (random in 0.0..30.0) {
            Toast.makeText(this, "Fail :( Try again. Total: " + userList.resource_iron, Toast.LENGTH_LONG).show()
        }
        if (random > 30 && random <= 40) {
            if (userList.resource_iron + 1 <= userList.cargo) {
                Toast.makeText(this, "You got 1 iron. Total: " + (userList.resource_iron + 1), Toast.LENGTH_LONG).show()
                documentReferenceInventory!!.update("Iron", userList.resource_iron + 1)
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show()
            }

        }
        if (random > 40 && random <= 60) {
            if (userList.resource_iron + 2 <= userList.cargo) {
                Toast.makeText(this, "You got 2 iron. Total: " + (userList.resource_iron + 2), Toast.LENGTH_LONG).show()
                documentReferenceInventory!!.update("Iron", userList.resource_iron + 2)
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show()
            }

        }
        if (random > 60 && random <= 80) {

            if (userList.resource_iron + 3 <= userList.cargo) {
                Toast.makeText(this, "You got 3 iron. Total: " + (userList.resource_iron + 3), Toast.LENGTH_LONG).show()
                documentReferenceInventory!!.update("Iron", userList.resource_iron + 3)
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show()
            }

        }
        if (random > 80 && random <= 90) {
            if (userList.resource_iron + 5 <= userList.cargo) {
                Toast.makeText(this, "You got 5 iron. Total: " + (userList.resource_iron + 5), Toast.LENGTH_LONG).show()
                documentReferenceInventory!!.update("Iron", userList.resource_iron + 5)
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show()
            }

        }
        if (random > 90 && random <= 95) {
            if (userList.resource_iron + 10 <= userList.cargo) {
                Toast.makeText(this, "You got 10 iron. Total: " + (userList.resource_iron + 10), Toast.LENGTH_LONG).show()
                documentReferenceInventory!!.update("Iron", userList.resource_iron + 10)
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show()
            }

        }
        if (random > 95 && random <= 97.5) {
            if (userList.resource_iron + 50 <= userList.cargo) {
                Toast.makeText(this, "You got 50 iron. Total: " + (userList.resource_iron + 50), Toast.LENGTH_LONG).show()
                documentReferenceInventory!!.update("Iron", userList.resource_iron + 50)
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show()
            }

        }
        if (random > 97.5 && random <= 100) {
            if (userList.resource_iron + 100 <= userList.cargo) {
                Toast.makeText(this, "You got 100 iron. Total: " + (userList.resource_iron + 100), Toast.LENGTH_LONG).show()
                documentReferenceInventory!!.update("Iron", userList.resource_iron + 100)
            } else {
                Toast.makeText(this, "Your cargo is full!", Toast.LENGTH_LONG).show()
            }
        }


    }

    fun onJump(view: View) {

        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
            //maxTimeInMilliseconds/1000 indicates how much resources are going to be mined until the cargo is fulled
            documentReferenceInventory!!.update("Iron", userList.resource_iron + maxTimeInMilliseconds / 1000 - remainedSecs)
            documentReferencePlanet!!.update("iron", objectModel.debrisIronAmount - (maxTimeInMilliseconds / 1000 - remainedSecs))
            gate_action.text = resources.getString(R.string.mine)
            return
        }

        when (userList.moveToObjectType) {
            "planet" -> {

                val intent = Intent(this, PlanetActivity::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                } else {
                    startActivity(intent)
                }
            }
            "user" -> gate_action.text = resources.getString(R.string.fight)
            "fuel" -> {
                val fuelToFill = 20 - userList.fuel
                val price = fuelToFill * 1000
                if (userList.money >= price) {
                    documentReference!!.update("fuel", 20)
                    documentReference!!.update("money", userList.money - price)
                }
            }
            "debris" -> mineDebris()
            "meteorite_field" -> mineIron()
        }
    }

    private fun createDebris() {
        val debris = HashMap<String, Any>()
        val x = RandomUtils.getRandomCoordinate()
        val y = RandomUtils.getRandomCoordinate()
        debris["x"] = x
        debris["y"] = y
        debris["sumXY"] = x + y
        debris["type"] = "debris"
        debris["iron"] = RandomUtils.getRandomDebrisIron()

        val collectionReferencePlanet = firebaseFirestore.collection("Objects")
        collectionReferencePlanet.document("Debris-" + RandomUtils.getRandomDebrisName(4))
                .set(debris)
    }


    private fun startTimer(finish: Long, tick: Long, debrisIsOver: Boolean) {

        countDownTimer = object : CountDownTimer(finish, tick) {

            override fun onTick(millisUntilFinished: Long) {
                remainedSecs = millisUntilFinished / 1000
                gate_action.text = resources.getString(R.string.mine_stop) + " " + remainedSecs / 60 + ":" + remainedSecs % 60

            }

            override fun onFinish() {

                documentReferenceInventory!!.update("Iron", userList.resource_iron + maxTimeInMilliseconds / 1000)
                if (debrisIsOver) {
                    documentReferencePlanet!!.delete().addOnSuccessListener {
                        Toast.makeText(this@GateActivity, "Debris is empty and is not available anymore", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@GateActivity, MainOptionsActivity::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GateActivity).toBundle())
                        } else {
                            startActivity(intent)
                        }
                    }
                } else {
                    documentReferencePlanet!!.update("iron", objectModel.debrisIronAmount - maxTimeInMilliseconds / 1000)
                    Toast.makeText(this@GateActivity, "Mining is over. Cargo is full", Toast.LENGTH_SHORT).show()
                }
                gate_action.text = resources.getString(R.string.mine)
                cancel()
                countDownTimer = null

            }
        }.start()
    }

    private fun mineDebris() {

        debrisIsOver = false
        maxTimeInMilliseconds = (1000 * (userList.cargo - userList.resource_iron)).toLong()

        // If the iron amount on debris less than an amount that is needed to fill up fully the cargo

        if (objectModel.debrisIronAmount < maxTimeInMilliseconds / 1000) {
            maxTimeInMilliseconds = (1000 * objectModel.debrisIronAmount).toLong()
            debrisIsOver = true
            createDebris()
        }
        startTimer(maxTimeInMilliseconds, 1000, debrisIsOver)

    }

    fun onScan(view: View) {

        val intent = Intent(this, ScanResultActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    companion object {

        private val TAG = "myLogs"
    }
}
