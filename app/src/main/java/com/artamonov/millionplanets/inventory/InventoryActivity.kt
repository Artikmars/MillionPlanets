package com.artamonov.millionplanets.inventory

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.inventory.presenter.InventoryActivityPresenter
import com.artamonov.millionplanets.inventory.presenter.InventoryActivityPresenterImpl
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.NumberPickerDialogType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.inventory_activity.*

class InventoryActivity : BaseActivity(R.layout.inventory_activity), InventoryActivityView, InventoryWeaponAdapter.ItemClickListener,
InventoryCargoAdapter.ItemClickListener, NumberPickerDialog.NumberPickerDialogListener {

    private var documentReference: DocumentReference? = null
    internal var userList: User? = User()

    lateinit var presenter: InventoryActivityPresenter<InventoryActivityView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                    override fun get(position: Int): Item {
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
                    .newInstance(NumberPickerDialogType.INVENTORY, presenter.getResourceAmount(position)!!.toInt(),
                            presenter.getCargoItem(position).itemId!!.toInt(), this)
            fragment.show(supportFragmentManager)
        }
    }

    private fun onDialogCreate() {
            val fragment = NumberPickerDialog
                    .newInstance(NumberPickerDialogType.GET_FUEL, presenter.getAvailablePetrolAmountToBeFilled()?.toInt(), 5, this)
            fragment.show(supportFragmentManager)
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
                setGetFuelButtonVisibility()
                inventory_fuel.text = presenter.getUserList().fuel.toString()
            }
        }
    }

    private fun setGetFuelButtonVisibility() {
        inventory_get_fuel.setOnClickListener {
            if (presenter.isPetrolAvailable() && !presenter.isFuelFull()) {
                    onDialogCreate() } else {
                Snackbar.make(findViewById(android.R.id.content), "Fuel can not be refilled", Snackbar.LENGTH_SHORT)
                        .show()
            }
        }
    }

    override fun updateCargoCapacityCounter(user: User) {
        val currentCargoCapacity = user.cargo?.sumBy { it.itemAmount!!.toInt() }
        inventory_capacity_label.text = currentCargoCapacity.toString() + "/" + user.cargoCapacity
        if (currentCargoCapacity!! > user.cargoCapacity) {
            inventory_capacity_label.setTextColor(resources.getColor(R.color.red))
        } else {
            inventory_capacity_label.setTextColor(resources.getColor(R.color.white))
        }
    }

    override fun updateData() {
        val batch = firebaseFirestore.batch()
        batch.update(documentReference!!, "weapon", presenter.getWeaponList())
        batch.commit()
    }

    override fun setProgressBar() {
    }

    companion object {
        private val TAG = "myLogs"
        const val ENEMY_USERNAME = "ENEMY_USERNAME"
        const val RESOURCE_AMOUNT = "RESOURCE_AMOUNT"
        const val RESOURCE_ID = "RESOURCE_ID"
        const val NUMBER_PICKER_DIALOG_TYPE = "NUMBER_PICKER_DIALOG_TYPE"
    }

    override fun onDismiss() {
        updateData()
        setGetFuelButtonVisibility()
    }
}
