package com.artamonov.millionplanets.sectors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import java.util.ArrayList

class SectorsYouFragment : Fragment(), SectorsYouAdapter.DialogListener {
    var firebaseFirestore: FirebaseFirestore? = null
    private var userList: MutableList<User>? = null
    private var spaceObjectList: MutableList<SpaceObject>? = null
    private var firebaseUser: FirebaseUser? = null
    private var documentReferenceInventory: DocumentReference? = null
    private var rvSectors: RecyclerView? = null
    private var btnAction: TextView? = null
    private var documentReferenceUser: DocumentReference? = null
    private fun setButtonEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            btnAction!!.isEnabled = true
            btnAction!!.setBackgroundColor(resources.getColor(R.color.colorAccent))
        } else {
            btnAction!!.isEnabled = false
            btnAction!!.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            Log.i("myTags", "setUserVisibleHint ")
            firebaseFirestore = FirebaseFirestore.getInstance()
            firebaseUser = FirebaseAuth.getInstance().currentUser
            documentReferenceInventory = firebaseFirestore!!
                    .collection("Inventory")
                    .document(firebaseUser!!.displayName!!)
            documentReferenceUser = firebaseFirestore!!.collection("Objects").document(firebaseUser!!.displayName!!)
            firebaseFirestore!!
                    .runTransaction<Void> { transaction ->
                        val spaceObject = SpaceObject()
                        val user = User()
                        val documentSnapshot1 = transaction[documentReferenceUser!!]
                        user.locationName = documentSnapshot1.getString("locationName")
                        user.money = documentSnapshot1.getLong("money")
                        val documentSnapshotInventory = transaction[documentReferenceInventory!!]
                        user.sectors = documentSnapshotInventory
                                .getLong(user.locationName!!)
                        val documentReferencePlanetMarket = firebaseFirestore!!
                                .collection("Objects")
                                .document(user.locationName!!)
                        val documentSnapshot2 = transaction[documentReferencePlanetMarket]
                        spaceObject.planetSectorsPrice = documentSnapshot2.getLong("sectors_price")!!.toInt().toLong()
                        spaceObject.planetSectors = documentSnapshot2.getLong("sectors")!!.toInt().toLong()
                        userList = ArrayList()
                        userList?.add(user)
                        spaceObjectList = ArrayList()
                        spaceObjectList?.add(spaceObject)
                        transaction.update(
                                documentReferenceInventory!!,
                                user.locationName!!,
                                user.sectors)
                        transaction.update(
                                documentReferenceUser!!,
                                "locationName",
                                user.locationName)
                        transaction.update(
                                documentReferencePlanetMarket,
                                "sectors",
                                spaceObject.planetSectors)
                        null
                    }
                    .addOnSuccessListener {
                        setAdapter()
                        if (userList!![0].sectors == 0L) {
                            setButtonEnabled(false)
                        } else {
                            setButtonEnabled(true)
                        }
                    }
            /*  documentReferenceInventory.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = new User();
                    user.setResource_iron(documentSnapshot.getLong("iron").intValue());
                    userList = new ArrayList<>();
                    userList.add(user);
                    setAdapter();


                }
            });*/
        }
    }

    private fun setAdapter() {
        val sectorsYouAdapter = SectorsYouAdapter(userList!!, spaceObjectList!!, activity!!, this)
        sectorsYouAdapter.notifyDataSetChanged()
        rvSectors!!.adapter = sectorsYouAdapter
        rvSectors!!.layoutManager = LinearLayoutManager(activity)
        Log.i("myTags", "userList size: " + userList!!.size)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sectors, container, false)
        Log.i("myTags", "ON CREATE VIEW: YOU: ")
        rvSectors = view.findViewById(R.id.rvSectors)
        btnAction = view.findViewById(R.id.sections_action_btn)
        btnAction?.text = resources.getString(R.string.sectors_action_sell)
        btnAction?.setOnClickListener(
                View.OnClickListener {
                    if (userList!![0].sectors == 2L) {
                        val sectorsYouDialog = SectorsYouDialog()
                        sectorsYouDialog.show(fragmentManager!!, "text")
                        return@OnClickListener
                    }
                    firebaseFirestore
                            ?.runTransaction<Void>(
                                    Transaction.Function { transaction ->
                                        val documentReferencePlanetMarket = firebaseFirestore
                                                ?.collection("Objects")
                                                ?.document(
                                                        userList!![0]
                                                                .locationName!!)
                                        transaction.update(
                                                documentReferenceInventory!!,
                                                userList!![0].locationName!!,
                                                0)
                                        transaction.update(
                                                documentReferenceUser!!,
                                                "money", userList!![0].money!! +
                                                spaceObjectList
                                                ?.get(0)?.planetSectorsPrice!! /
                                                2)
                                        transaction.update(
                                                documentReferencePlanetMarket!!,
                                                "sectors", spaceObjectList!![0].planetSectors +
                                                1)
                                        null
                                    })
                            ?.addOnSuccessListener {
                                Snackbar.make(
                                        activity
                                                !!.findViewById(
                                                        android.R.id.content),
                                        "You have sold 1 sector",
                                        Snackbar.LENGTH_LONG)
                                        .show()
                                btnAction?.isEnabled = false
                                btnAction?.setBackgroundColor(
                                        resources.getColor(R.color.grey))
                            }
                })
        return view
    }

    override fun onDialogCreate() {}

    companion object {
        @JvmStatic
        fun newInstance(): SectorsYouFragment {
            val fragment = SectorsYouFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}