package com.artamonov.millionplanets.inventory

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.inventory.presenter.InventoryActivityPresenter
import com.artamonov.millionplanets.inventory.presenter.InventoryActivityPresenterImpl
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.inventory.*

class InventoryActivity : BaseActivity(), InventoryActivityView, InventoryWeaponAdapter.ItemClickListener,
InventoryCargoAdapter.ItemClickListener {

    private var documentReference: DocumentReference? = null
    internal var userList: User? = User()

    lateinit var presenter: InventoryActivityPresenter<InventoryActivityView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inventory)
        presenter = InventoryActivityPresenterImpl(this)
    }

    private fun setUpWeaponsAdapter() {
        rv_inventory.layoutManager = LinearLayoutManager(this)
        rv_inventory.adapter = InventoryWeaponAdapter(
                object : InventoryWeaponSource {

                    override fun isInstalled(position: Int): Boolean {
                        return presenter.isInstalled(position)
                    }

                    override fun getCount(): Int {
                        return presenter.getWeaponAmount()
                    }

                    override fun getWeaponList(): List<Weapon> {
                        return presenter.getWeaponList()
                    }

                    override fun get(position: Int): Long {
                        return presenter.getWeaponItem(position)
                    }

                    override fun setEnabled(position: Int, boolean: Boolean) {
                        return presenter.setEnabled(position, boolean)
                    }
                }, this@InventoryActivity)
    }

    private fun setUpCargoAdapter() {
        rv_cargo.layoutManager = LinearLayoutManager(this)
        val cargoAdapter = InventoryCargoAdapter(
                object : InventoryCargoSource {

                    override fun getCount(): Int {
                        return presenter.getCargoAmount()
                    }

                    override fun getItemAmount(position: Int): Long? {
                        return presenter.getResourceAmount(position)
                    }

                    override fun getCargoList(): List<Item>? {
                        return presenter.getCargoList()
                    }

                    override fun get(position: Int): Long {
                        return presenter.getCargoItem(position)
                    }
                }, this@InventoryActivity)
        rv_cargo.adapter = cargoAdapter
        cargoAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int, isChecked: Boolean) {
    }

    override fun onDialogCreate(position: Int) {
        if (presenter.getResourceAmount(position) != 0L) {
            val fragment = NumberPickerDialog
                    .newInstance(presenter.getResourceAmount(position)!!.toInt(),
                            position)
            fragment.show(supportFragmentManager)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.initFirebase()
        documentReference = firebaseFirestore.collection("Objects")
                .document(firebaseUser?.displayName!!)
        presenter.initData()
        documentReference!!.addSnapshotListener(this) { doc, _ ->
            if (doc!!.exists()) {
                presenter.initUserList(doc)
                setUpWeaponsAdapter()
                setUpCargoAdapter()
            }
        }
    }

    override fun updateData() {
        val batch = firebaseFirestore.batch()
        batch.update(documentReference!!, "weapon", presenter.getWeaponList())
        batch.commit()
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                }
    }

    override fun setProgressBar() {
    }

    companion object {
        private val TAG = "myLogs"
        const val ENEMY_USERNAME = "ENEMY_USERNAME"
        const val RESOURCE_AMOUNT = "RESOURCE_AMOUNT"
        const val RESOURCE_POSITION = "RESOURCE_POSITION"
    }
}
