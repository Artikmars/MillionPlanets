package com.artamonov.millionplanets.inventory

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.databinding.ActivityInventoryBinding
import com.artamonov.millionplanets.inventory.presenter.InventoryActivityPresenter
import com.artamonov.millionplanets.inventory.presenter.InventoryActivityPresenterImpl
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.NumberPickerDialogType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.Weapon
import com.artamonov.millionplanets.utils.showSnackbarError

class InventoryActivity : BaseActivity(), InventoryActivityView, InventoryWeaponAdapter.ItemClickListener,
InventoryCargoAdapter.ItemClickListener, NumberPickerDialog.NumberPickerDialogListener {

    internal var userList: User? = User()
    lateinit var binding: ActivityInventoryBinding
    lateinit var presenter: InventoryActivityPresenter<InventoryActivityView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = InventoryActivityPresenterImpl(this)
    }

    private fun setUpWeaponsAdapter() {
        binding.rvInventory.layoutManager = LinearLayoutManager(this)
        binding.rvInventory.adapter = InventoryWeaponAdapter(
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
        binding.rvCargo.layoutManager = LinearLayoutManager(this)
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
        binding.rvCargo.adapter = cargoAdapter
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
        userDocument.addSnapshotListener(this) { doc, _ ->
            if (doc!!.exists()) {
                presenter.initUserList(doc)
                setUpWeaponsAdapter()
                setUpCargoAdapter()
                setGetFuelButtonVisibility()
                binding.inventoryFuel.text = presenter.getUserList().fuel.toString()
            }
        }
    }

    private fun setGetFuelButtonVisibility() {
        if (presenter.isFuelFull() || !presenter.isPetrolAvailable()) {
            binding.inventoryGetFuel.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
        } else {
            binding.inventoryGetFuel.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        }
        binding.inventoryGetFuel.setOnClickListener {
            if (presenter.isPetrolAvailable() && !presenter.isFuelFull()) {
                    onDialogCreate() } else {
                showSnackbarError(getString(R.string.inventory_fuel_can_not_be_refilled))
            }
        }
    }

    override fun updateCargoCapacityCounter(user: User) {
        val currentCargoCapacity = user.cargo?.sumBy { it.itemAmount!!.toInt() }
        binding.inventoryCapacityLabel.text = currentCargoCapacity.toString() + "/" + user.cargoCapacity
        if (currentCargoCapacity!! > user.cargoCapacity!!) {
            binding.inventoryCapacityLabel.setTextColor(ContextCompat.getColor(this, R.color.red))
        } else {
            binding.inventoryCapacityLabel.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    override fun updateData() {
        val batch = firebaseFirestore.batch()
        batch.update(userDocument, "weapon", presenter.getWeaponList())
        batch.commit()
    }

    override fun setProgressBar() {
    }

    companion object {
        const val RESOURCE_AMOUNT = "RESOURCE_AMOUNT"
        const val RESOURCE_ID = "RESOURCE_ID"
        const val NUMBER_PICKER_DIALOG_TYPE = "NUMBER_PICKER_DIALOG_TYPE"
    }

    override fun onDismiss() {
        updateData()
        setGetFuelButtonVisibility()
    }
}
