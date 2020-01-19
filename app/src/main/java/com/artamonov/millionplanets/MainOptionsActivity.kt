package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.inventory.InventoryActivity
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.main_options.*
import java.util.HashMap

class MainOptionsActivity : BaseActivity() {

    internal var userList = User()
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_options)

        val parentLayout = findViewById<View>(android.R.id.content)
        Snackbar.make(
                parentLayout,
                "Welcome on board, " + firebaseUser!!.displayName + "!",
                Snackbar.LENGTH_LONG)
                .show()

        scan.setOnClickListener {
            if (cargoIsOverloaded()) {
                Snackbar.make(parentLayout,
                        "Cargo is overloaded! Please, drop extra items through Inventory menu.",
                        Snackbar.LENGTH_LONG)
                        .show()
                return@setOnClickListener }
            val intent = Intent(this, ScanResultActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }

        inventory.setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }
        /*   documentReference = firebaseFirestore.collection("Objects").document(firebaseUser.getDisplayName());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    userList.setShip(doc.getString("ship"));
                    //    userList.setPosition(doc.getString("position"));
                    userList.setX((doc.getLong("x").intValue()));
                    userList.setY((doc.getLong("y").intValue()));
                    userList.setHp(doc.getLong("hp").intValue());
                    userList.setCargoCapacity(doc.getLong("cargoCapacity").intValue());
                    userList.setFuel(doc.getLong("fuel").intValue());
                    userList.setScanner_capacity(doc.getLong("scanner_capacity").intValue());
                    userList.setShield(doc.getLong("shield").intValue());
                    userList.setMoney(doc.getLong("money").intValue());


                    tvPosition.setText(String.format(getResources().getString(R.string.current_coordinate),
                            userList.getX(), userList.getY()));
                    tvShip.setText(userList.getShip());
                    tvHp.setText(Integer.toString(userList.getHp()));
                    tvShield.setText(Integer.toString(userList.getShield()));
                    tvCargo.setText(Integer.toString(userList.getCargoCapacity()));
                    tv_ScannerCapacity.setText(Integer.toString(userList.getScanner_capacity()));
                    tvFuel.setText(Integer.toString(userList.getFuel()));
                    progressDialog.dismiss();
                }
            }
        });*/
    }

    private fun cargoIsOverloaded(): Boolean {
        return Utils.getCurrentCargoCapacity(userList) > userList.cargoCapacity
    }

    override fun onStart() {
        super.onStart()
        progressDialog = ProgressDialog(this)
        progressDialog?.setCancelable(false)
        progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog?.show()
        createDocumentReference()
    }

    override fun onResume() {
        super.onResume()
        progressDialog?.dismiss()
    }

    private fun createDocumentReference() {
        val documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                setDefaultValues(doc)
            } else {
                createNewUserObject()
            }
        }
    }

    private fun createNewUserObject() {
        val documentReferenceInventory = firebaseFirestore.collection("Inventory").document(firebaseUser!!.displayName!!)
        val documentReferenceObjects = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        val user = User()
        user.x = 5
        user.y = 6
        user.cargoCapacity = 10
        user.hp = 50
        user.ship = "Fighter"
        user.money = 1000
        user.scanner_capacity = 15
        user.shield = 100
        user.jump = 10
        user.fuel = 20
        user.type = "user"
        user.sumXY = 11
        firebaseFirestore
                .runTransaction { transaction ->
                    transaction.set(documentReferenceObjects, user)
                    val ironData = HashMap<String, Any>()
                    ironData["Iron"] = 0
                    ironData["Mercaster"] = 0
                    ironData["Leabia"] = 0
                    ironData["Cracaphill"] = 0
                    transaction.set(documentReferenceInventory, ironData)
                    null
                }
                .addOnSuccessListener { createDocumentReference() }
    }

    private fun setDefaultValues(doc: DocumentSnapshot) {
        userList = doc.toObject(User::class.java)!!

        coordinates.text = String.format(
                resources.getString(R.string.current_coordinate),
                userList.x,
                userList.y)
        ship.text = userList.ship
        hp.text = userList.hp.toString()
        shield.text = userList.shield.toString()
        cargo.text = userList.cargoCapacity.toString()
        scanner_capacity.text = userList.scanner_capacity.toString()
        fuel.text = userList.fuel.toString()
        money.text = userList.money.toString()

        progressDialog?.dismiss()
    }
}
