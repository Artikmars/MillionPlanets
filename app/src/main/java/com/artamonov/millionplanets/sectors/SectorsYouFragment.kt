package com.artamonov.millionplanets.sectors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sectors.*

class SectorsYouFragment : Fragment(), SectorsYouAdapter.DialogListener {
    var firebaseFirestore: FirebaseFirestore? = null
    private var firebaseUser: FirebaseUser? = null
    private var documentReferenceUser: DocumentReference? = null
    private var documentReferenceObject: DocumentReference? = null
    private var spaceObject: SpaceObject? = null
    private var user: User? = null

    private fun setButtonEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            sections_action_btn.isEnabled = true
            sections_action_btn.setBackgroundColor(resources.getColor(R.color.colorAccent))
        } else {
            sections_action_btn.isEnabled = false
            sections_action_btn.setBackgroundColor(resources.getColor(R.color.grey))
        }
    }

//    override fun setMenuVisibility(menuVisible: Boolean) {
//        super.setMenuVisibility(menuVisible)
//        if (menuVisible) {
//            sections_action_btn.text = resources.getString(R.string.sectors_action_sell)
//            firebaseFirestore = FirebaseFirestore.getInstance()
//            firebaseUser = FirebaseAuth.getInstance().currentUser
//            documentReferenceUser = firebaseFirestore!!.collection("Objects").document(firebaseUser!!.displayName!!)
//            firebaseFirestore?.runTransaction<Void> { transaction ->
//                val documentReferenceUserSnapshot = transaction[documentReferenceUser!!]
//                user = documentReferenceUserSnapshot.toObject(User::class.java)!!
//                null
//            }
//                    ?.addOnSuccessListener {
//                        sectors_amount.text = getString(R.string.sectors_amount,
//                                user?.sectors?.size)
//                        if (user?.sectors?.size!! > 0 ) {
//                            setButtonEnabled(true)
//                        } else {
//                            setButtonEnabled(false)
//                        }
//                    }
//        }
//    }

    private fun init() {
        sections_action_btn.text = resources.getString(R.string.sectors_action_sell)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        documentReferenceUser = firebaseFirestore!!.collection("Objects").document(firebaseUser!!.displayName!!)
        firebaseFirestore?.runTransaction<Void> { transaction ->
            val documentReferenceUserSnapshot = transaction[documentReferenceUser!!]
            user = documentReferenceUserSnapshot.toObject(User::class.java)!!
            null
        }
                ?.addOnSuccessListener {
                    val userSectorObj = user?.sectors
                            ?.find { it.planetName == user?.moveToLocationName }
                    if (userSectorObj != null && userSectorObj.amount > 0) {
                        sectors_amount.text = getString(R.string.sectors_amount, userSectorObj.amount)
                        setButtonEnabled(true)
                    } else {
                        setButtonEnabled(false)
                    }
                }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sectors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        sections_action_btn.setOnClickListener {
            val documentReferencePlanet = firebaseFirestore!!
                    .collection("Objects").document(user?.locationName!!)
            firebaseFirestore?.runTransaction<Void> { transaction ->
                val documentReferencePlanetSnapshot = transaction[documentReferencePlanet]
                spaceObject = documentReferencePlanetSnapshot.toObject(SpaceObject::class.java)!!
                transaction.update(
                        documentReferenceUser!!,
                        "money", user?.money!! +
                        spaceObject?.planetSectorsPrice!! / 2)
                user?.sectors?.removeAll { it.planetName == user?.locationName }
                // user?.sectors?.map { if (it.planetName == user?.locationName) it.amount -- }
                transaction.update(
                        documentReferenceUser!!,
                        "sectors", user?.sectors)
                transaction.update(
                        documentReferencePlanet,
                        "availableSectors", 2)
                null
            }
                    ?.addOnSuccessListener {
                        activity?.showSnackbarError(getString(R.string.sectors_you_sold_sectors))
                        sections_action_btn.isEnabled = false
                        sections_action_btn.setBackgroundColor(
                                resources.getColor(R.color.grey))
                    }
        }
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