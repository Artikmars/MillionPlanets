package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import java.util.HashMap

class MainOptionsActivity : BaseActivity() {

    internal var userList = User()
    private var progressDialog: ProgressDialog? = null
    private var tvPosition: TextView? = null
    private var tvShip: TextView? = null
    private var tvHp: TextView? = null
    private var tvShield: TextView? = null
    private var tvCargo: TextView? = null
    private var tv_ScannerCapacity: TextView? = null
    private var tvFuel: TextView? = null
    private var tvMoney: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_options)

        tvPosition = findViewById(R.id.coordinates)
        tvShip = findViewById(R.id.ship)
        tvHp = findViewById(R.id.hp)
        tvShield = findViewById(R.id.shield)
        tvCargo = findViewById(R.id.cargo)
        tv_ScannerCapacity = findViewById(R.id.scanner_capacity)
        tvFuel = findViewById(R.id.fuel)
        tvMoney = findViewById(R.id.money)

        /*  progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();*/

        val parentLayout = findViewById<View>(android.R.id.content)
        Snackbar.make(
                parentLayout,
                "Welcome on board, " + firebaseUser!!.displayName + "!",
                Snackbar.LENGTH_LONG)
                .show()

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
                    userList.setCargo(doc.getLong("cargo").intValue());
                    userList.setFuel(doc.getLong("fuel").intValue());
                    userList.setScanner_capacity(doc.getLong("scanner_capacity").intValue());
                    userList.setShield(doc.getLong("shield").intValue());
                    userList.setMoney(doc.getLong("money").intValue());


                    tvPosition.setText(String.format(getResources().getString(R.string.current_coordinate),
                            userList.getX(), userList.getY()));
                    tvShip.setText(userList.getShip());
                    tvHp.setText(Integer.toString(userList.getHp()));
                    tvShield.setText(Integer.toString(userList.getShield()));
                    tvCargo.setText(Integer.toString(userList.getCargo()));
                    tv_ScannerCapacity.setText(Integer.toString(userList.getScanner_capacity()));
                    tvFuel.setText(Integer.toString(userList.getFuel()));
                    progressDialog.dismiss();
                }
            }
        });*/
    }

    override fun onStart() {
        super.onStart()
        progressDialog = ProgressDialog(this)
        // progressDialog.setMessage("Loading Data . . .");
        // progressDialog.setTitle("In Progress");
        progressDialog?.setCancelable(false)
        // progressDialog.setMax(100);
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
        user.cargo = 10
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
        userList.ship = doc.getString("ship")
        userList.x = doc.getLong("x")!!
        userList.y = doc.getLong("y")!!
        userList.sumXY = doc.getLong("sumXY")!!
        userList.hp = doc.getLong("hp")!!
        userList.cargo = doc.getLong("cargo")!!
        userList.fuel = doc.getLong("fuel")!!
        userList.scanner_capacity = doc.getLong("scanner_capacity")!!
        userList.shield = doc.getLong("shield")!!
        userList.money = doc.getLong("money")!!
        userList.moveToObjectName = doc.getString("moveToObjectName")
        userList.moveToObjectType = doc.getString("moveToObjectType")

        tvPosition!!.text = String.format(
                resources.getString(R.string.current_coordinate),
                userList.x,
                userList.y)
        tvShip!!.text = userList.ship
        tvHp!!.text = userList.hp.toString()
        tvShield!!.text = userList.shield.toString()
        tvCargo!!.text = userList.cargo.toString()
        tv_ScannerCapacity!!.text = userList.scanner_capacity.toString()
        tvFuel!!.text = userList.fuel.toString()
        tvMoney!!.text = userList.money.toString()

        progressDialog?.dismiss()
    }

    fun onScan(view: View) {
        val intent = Intent(this, ScanResultActivity::class.java)
        /*  intent.putExtra("x", userList.getX());
        intent.putExtra("y", userList.getY());
        intent.putExtra("hull", userList.getHp());
        intent.putExtra("cargo", userList.getCargo());
        intent.putExtra("scanner_capacity", userList.getScanner_capacity());
        intent.putExtra("shield", userList.getShield());
        intent.putExtra("ship", userList.getShip());
        intent.putExtra("fuel", userList.getFuel());*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }
}
