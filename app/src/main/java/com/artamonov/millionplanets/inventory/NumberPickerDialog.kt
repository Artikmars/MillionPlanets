package com.artamonov.millionplanets.inventory

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.artamonov.millionplanets.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class NumberPickerDialog : DialogFragment() {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    private var currentAmount: Int? = null
    private var position: Int? = null
    private var documentReferenceUser: DocumentReference? = null

    fun show(fragmentManager: FragmentManager?) {
        super.show(fragmentManager!!, TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentAmount = arguments!!.getInt(InventoryActivity.RESOURCE_AMOUNT)
        position = arguments!!.getInt(InventoryActivity.RESOURCE_POSITION)
        documentReferenceUser = firebaseFirestore.collection("Objects")
                .document(firebaseUser?.displayName!!)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)
        numberPicker.minValue = 1
        numberPicker.maxValue = currentAmount ?: 1
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Value")
        builder.setMessage("Choose amount of the resource you want to drop :")
        builder.setPositiveButton("OK") { _, _ ->
            firebaseFirestore.runTransaction { transaction ->
                val documentSnapshot = transaction[documentReferenceUser!!]
                val userList = documentSnapshot.toObject(User::class.java)!!
                userList.cargo?.get(position!!)?.itemAmount = numberPicker.maxValue -
                        numberPicker.value.toLong()
                transaction.set(documentReferenceUser!!, userList)
                dismiss()
            }
            //                valueChangeListener.onValueChange(numberPicker,
//                        numberPicker.getValue(), numberPicker.getValue());
        }
        builder.setNegativeButton("CANCEL") { _, _ -> dismiss() }
        builder.setView(numberPicker)
        return builder.create()
    } //    public NumberPicker.OnValueChangeListener getValueChangeListener() {

    //        return valueChangeListener;
//    }
//
//    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
//        this.valueChangeListener = valueChangeListener;
//    }
    companion object {

        private val TAG = "AttachmentPhotoDialogFragment"

        fun newInstance(currentAmount: Int, position: Int): NumberPickerDialog {
            val args = Bundle()
            args.putInt(InventoryActivity.RESOURCE_AMOUNT, currentAmount)
            args.putInt(InventoryActivity.RESOURCE_POSITION, position)
            val fragment = NumberPickerDialog()
            fragment.arguments = args
            return fragment
        }
    }
}