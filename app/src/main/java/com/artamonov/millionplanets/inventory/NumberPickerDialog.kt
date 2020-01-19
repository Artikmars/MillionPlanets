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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class NumberPickerDialog(private val listener: NumberPickerDialogListener) : DialogFragment() {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    private var currentAmount: Int? = null
    private var resourceId: Int? = null
    private var resourceIndex: Int? = null
    private var type: String? = null
    private var documentReferenceUser: DocumentReference? = null

    fun show(fragmentManager: FragmentManager?) {
        super.show(fragmentManager!!, TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentAmount = arguments!!.getInt(InventoryActivity.RESOURCE_AMOUNT)
        resourceId = arguments!!.getInt(InventoryActivity.RESOURCE_ID)
        type = arguments!!.getString(InventoryActivity.NUMBER_PICKER_DIALOG_TYPE)
        documentReferenceUser = firebaseFirestore.collection("Objects")
                .document(firebaseUser?.displayName!!)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)
        numberPicker.minValue = 1
        numberPicker.maxValue = currentAmount ?: 1
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Value")

        when (type) {
            NumberPickerDialogType.INVENTORY ->
            {
                builder.setMessage("Choose amount of the resource you want to drop :")
                builder.setPositiveButton("OK") { _, _ ->
                    firebaseFirestore.runTransaction { transaction ->
                        val documentSnapshot = transaction[documentReferenceUser!!]
                        val userList = documentSnapshot.toObject(User::class.java)!!
                        resourceIndex = userList.cargo?.indexOfFirst { it.itemId == resourceId?.toLong() }
                        userList.cargo?.get(resourceIndex!!)?.itemAmount = numberPicker.maxValue -
                                numberPicker.value.toLong()
                        transaction.set(documentReferenceUser!!, userList)
                        listener.onDismiss()
                        dismiss()
                    }
                }
            }
            NumberPickerDialogType.MARKET_PLAYER_SELLS -> {
                builder.setMessage("Choose amount of the resource you want to sell :")
                builder.setPositiveButton("OK") { _, _ ->
                    firebaseFirestore.runTransaction { transaction ->
                        val documentSnapshot = transaction[documentReferenceUser!!]
                        val userList = documentSnapshot.toObject(User::class.java)!!
                        resourceIndex = userList.cargo?.indexOfFirst { it.itemId == resourceId?.toLong() }
                        userList.cargo?.get(resourceIndex!!)?.itemAmount = numberPicker.maxValue -
                                        numberPicker.value.toLong()
                        userList.money = userList.money + Price.getPlayerSellPrice(resourceId?.toLong()) *
                                numberPicker.value.toLong()
                        if (numberPicker.value == numberPicker.maxValue) {
                        userList.cargo?.removeAt(resourceIndex!!)
                        }
                        transaction.set(documentReferenceUser!!, userList)
                        listener.onDismiss()
                        dismiss()
                    }
                }
            }
            NumberPickerDialogType.MARKET_PLAYER_BUYS -> {
                builder.setMessage("Choose amount of the resource you want to buy :")
                builder.setPositiveButton("OK") { _, _ ->
                    firebaseFirestore.runTransaction { transaction ->
                        val documentSnapshot = transaction[documentReferenceUser!!]
                        val userList = documentSnapshot.toObject(User::class.java)!!

                        val item = userList.cargo?.find { it.itemId == resourceId?.toLong() }
                        item?.let {
                            userList.cargo?.get(resourceIndex!!)?.itemAmount =
                                    userList.cargo?.get(resourceIndex!!)?.itemAmount!! +
                                            numberPicker.value.toLong()
                            userList.money = userList.money - Price.getPlayerBuyPrice(resourceId) *
                                    numberPicker.value.toLong()
                            transaction.set(documentReferenceUser!!, userList)
                            listener.onDismiss()
                            dismiss()
                            return@runTransaction
                        }

                        val newItem = Item(resourceId?.toLong(), numberPicker.value.toLong())
                        userList.cargo!!.add(newItem)
                        userList.money = userList.money - Price.getPlayerBuyPrice(resourceId) *
                                numberPicker.value.toLong()
                        transaction.set(documentReferenceUser!!, userList)
                        listener.onDismiss()
                        dismiss()
                    }
                }
            }
        }

        builder.setNegativeButton("CANCEL") { _, _ -> dismiss() }
        builder.setView(numberPicker)
        return builder.create()
    }

    interface NumberPickerDialogListener {
        fun onDismiss()
    }

    companion object {

        private val TAG = "AttachmentPhotoDialogFragment"

        fun newInstance(
            @NumberPickerDialogType.AnnotationNumberPickerDialogType type: String,
            currentAmount: Int,
            itemId: Int,
            listener: NumberPickerDialogListener
        ): NumberPickerDialog {
            val args = Bundle()
            args.putString(InventoryActivity.NUMBER_PICKER_DIALOG_TYPE, type)
            args.putInt(InventoryActivity.RESOURCE_AMOUNT, currentAmount)
            args.putInt(InventoryActivity.RESOURCE_ID, itemId)
            val fragment = NumberPickerDialog(listener)
            fragment.arguments = args
            return fragment
        }
    }
}