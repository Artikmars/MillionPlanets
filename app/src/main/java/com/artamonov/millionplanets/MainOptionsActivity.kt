package com.artamonov.millionplanets

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle

import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.MainOptionsActivityBinding
import com.artamonov.millionplanets.inventory.InventoryActivity
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.scanresult.ScanResultActivity
import com.artamonov.millionplanets.utils.extensions.getCurrentCargoCapacity
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot

class MainOptionsActivity : BaseActivity() {

    internal var userList = User()
    lateinit var binding: MainOptionsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainOptionsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showSnackbarError(getString(R.string.main_welcome_on_board))

        binding.scan.setOnClickListener {
            if (cargoIsOverloaded()) {
                showSnackbarError(getString(R.string.main_cargo_is_overloaded))
                return@setOnClickListener }
            val intent = Intent(this, ScanResultActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            } else {
                startActivity(intent)
            }
        }

        binding.inventory.setOnClickListener {
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
        return userList.getCurrentCargoCapacity() > userList.cargoCapacity!!
    }

    override fun onStart() {
        super.onStart()
        val documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) { populateUI(task.result) }
        }
    }

    private fun populateUI(doc: DocumentSnapshot?) {
        userList = doc?.toObject(User::class.java)!!

        binding.coordinates.text = String.format(
                resources.getString(R.string.current_coordinate),
                userList.x,
                userList.y)
        binding.ship.text = userList.ship
        binding.hp.text = userList.hp.toString()
        binding.shield.text = userList.shield.toString()
        binding.cargo.text = userList.cargoCapacity.toString()
        binding.scannerCapacity.text = userList.scanner_capacity.toString()
        binding.fuel.text = userList.fuel.toString()
        binding.money.text = userList.money.toString()

        FirebaseCrashlytics.getInstance().log(userList.toString())
    }
}
