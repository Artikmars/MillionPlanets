package com.artamonov.millionplanets.sectors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sectors.*
import javax.inject.Inject

@AndroidEntryPoint
class SectorsYouFragment : Fragment(), SectorsYouAdapter.DialogListener {

    @Inject lateinit var firebaseFirestore: FirebaseFirestore
    @Inject lateinit var firebaseUser: FirebaseUser
    @Inject lateinit var userDocument: DocumentReference

    private var spaceObject: SpaceObject? = null
    private var user: User? = null

    private fun setButtonEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            sections_action_btn.isEnabled = true
            context?.let { sections_action_btn.setBackgroundColor(ContextCompat.getColor(it, R.color.colorAccent)) }
        } else {
            sections_action_btn.isEnabled = false
            context?.let { sections_action_btn.setBackgroundColor(ContextCompat.getColor(it, R.color.grey)) }
        }
    }

    private fun init() {
        sections_action_btn.text = resources.getString(R.string.sectors_action_sell)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.runTransaction<Void> { transaction ->
            val documentReferenceUserSnapshot = transaction[userDocument]
            user = documentReferenceUserSnapshot.toObject(User::class.java)!!
            null
        }
                .addOnSuccessListener {
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
            val documentReferencePlanet = firebaseFirestore
                    .collection("Objects").document(user?.locationName!!)
            firebaseFirestore.runTransaction<Void> { transaction ->
                val documentReferencePlanetSnapshot = transaction[documentReferencePlanet]
                spaceObject = documentReferencePlanetSnapshot.toObject(SpaceObject::class.java)!!
                transaction.update(
                        userDocument,
                        "money", user?.money!! +
                        spaceObject?.planetSectorsPrice!! / 2)
                user?.sectors?.removeAll { it.planetName == user?.locationName }
                // user?.sectors?.map { if (it.planetName == user?.locationName) it.amount -- }
                transaction.update(
                        userDocument,
                        "sectors", user?.sectors)
                transaction.update(
                        documentReferencePlanet,
                        "availableSectors", 2)
                null
            }
                    .addOnSuccessListener {
                        activity?.showSnackbarError(getString(R.string.sectors_you_sold_sectors))
                        sections_action_btn.isEnabled = false
                        context?.let { sections_action_btn.setBackgroundColor(
                                ContextCompat.getColor(it, R.color.grey)) }
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