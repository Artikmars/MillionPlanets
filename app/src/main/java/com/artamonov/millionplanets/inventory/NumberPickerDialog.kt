package com.artamonov.millionplanets.inventory

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.NumberPickerDialogType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Price
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NumberPickerDialog(private val listener: NumberPickerDialogListener) : DialogFragment() {
    private var currentAmount: Int? = null
    private var resourceId: Int? = null
    private var resourceIndex: Int? = null
    private var type: String? = null

    @Inject lateinit var firebaseUser: FirebaseUser
    @Inject lateinit var userDocument: DocumentReference
    @Inject lateinit var firebaseFirestore: FirebaseFirestore

    fun show(fragmentManager: FragmentManager?) {
        super.show(fragmentManager!!, TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentAmount = arguments?.getInt(InventoryActivity.RESOURCE_AMOUNT)
        resourceId = arguments?.getInt(InventoryActivity.RESOURCE_ID)
        type = arguments?.getString(InventoryActivity.NUMBER_PICKER_DIALOG_TYPE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)
        numberPicker.minValue = 1
        numberPicker.maxValue = currentAmount ?: 1
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Value")
        builder.setMessage(setBuilderMessage(type))

        builder.setPositiveButton("OK") { _, _ ->
            firebaseFirestore.runTransaction { transaction ->
                val documentSnapshot = transaction[userDocument!!]
                val userList = documentSnapshot.toObject(User::class.java)!!
                resourceIndex = userList.cargo?.indexOfFirst { it.itemId == resourceId?.toLong() }

                when (type) {
                    NumberPickerDialogType.GET_FUEL -> {
                        userList.fuel = userList.fuel!! + numberPicker.value.toLong()
                        userList.cargo?.get(resourceIndex!!)?.itemAmount =
                                userList.cargo?.get(resourceIndex!!)?.itemAmount!! - numberPicker.value.toLong()
                        if (numberPicker.value == numberPicker.maxValue) {
                            userList.cargo?.removeAt(resourceIndex!!)
                        }
                        updateData(transaction, userList)
                    }
                    NumberPickerDialogType.INVENTORY -> {
                        userList.cargo?.get(resourceIndex!!)?.itemAmount = numberPicker.maxValue -
                                numberPicker.value.toLong()
                        if (numberPicker.value == numberPicker.maxValue) {
                            userList.cargo?.removeAt(resourceIndex!!)
                        }
                        updateData(transaction, userList)
                    }
                    NumberPickerDialogType.MARKET_PLAYER_SELLS -> {
                        userList.cargo?.get(resourceIndex!!)?.itemAmount = numberPicker.maxValue -
                                numberPicker.value.toLong()
                        userList.money = userList.money!! + Price.getPlayerSellPrice(resourceId?.toLong()) *
                                numberPicker.value.toLong()
                        if (numberPicker.value == numberPicker.maxValue) {
                            userList.cargo?.removeAt(resourceIndex!!)
                        }
                        updateData(transaction, userList)
                    }
                    NumberPickerDialogType.MARKET_PLAYER_BUYS -> {
                        val item = userList.cargo?.find { it.itemId == resourceId?.toLong() }
                        item?.let {
                            userList.cargo?.get(resourceIndex!!)?.itemAmount =
                                    userList.cargo?.get(resourceIndex!!)?.itemAmount!! +
                                            numberPicker.value.toLong()
                            userList.money = userList.money!! - Price.getPlayerBuyPrice(resourceId) *
                                    numberPicker.value.toLong()
                            updateData(transaction, userList)
                            return@runTransaction
                        }

                        val newItem = Item(resourceId?.toLong(), numberPicker.value.toLong())
                        userList.cargo!!.add(newItem)
                        userList.money = userList.money!! - Price.getPlayerBuyPrice(resourceId) *
                                numberPicker.value.toLong()
                        updateData(transaction, userList)
                    }
                    else -> {
                        dismiss()
                    }
                }
            }
        }
        builder.setNegativeButton("CANCEL") { _, _ -> dismiss() }
        builder.setView(numberPicker)
        return builder.create()
    }

    private fun updateData(transaction: Transaction, userList: User) {
        transaction.set(userDocument!!, userList)
        listener.onDismiss()
        dismiss()
    }

    private fun setBuilderMessage(@NumberPickerDialogType.AnnotationNumberPickerDialogType message: String?): String {
        when (message) {
            NumberPickerDialogType.INVENTORY -> return "Choose amount of the resource you want to drop :"
            NumberPickerDialogType.MARKET_PLAYER_SELLS -> return "Choose amount of the resource you want to sell :"
            NumberPickerDialogType.MARKET_PLAYER_BUYS -> return "Choose amount of the resource you want to buy :"
        }
        return "Choose amount of the resource:"
    }

    interface NumberPickerDialogListener {
        fun onDismiss()
    }

    companion object {

        private const val TAG = "AttachmentPhotoDialogFragment"

        fun newInstance(
            @NumberPickerDialogType.AnnotationNumberPickerDialogType type: String,
            currentAmount: Int?,
            itemId: Int?,
            listener: NumberPickerDialogListener
        ): NumberPickerDialog {
            val args = Bundle()
            args.putString(InventoryActivity.NUMBER_PICKER_DIALOG_TYPE, type)
            currentAmount?.let { args.putInt(InventoryActivity.RESOURCE_AMOUNT, currentAmount) }
            itemId?.let { args.putInt(InventoryActivity.RESOURCE_ID, itemId) }
            val fragment = NumberPickerDialog(listener)
            fragment.arguments = args
            return fragment
        }
    }
}