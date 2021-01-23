package com.artamonov.millionplanets.sectors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.Sector
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.showSnackbarError
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sectors.*
import javax.inject.Inject

@AndroidEntryPoint
class SectorsPlanetFragment : Fragment(), SectorsPlanetAdapter.DialogListener {

    @Inject lateinit var userDocument: DocumentReference
    @Inject lateinit var firebaseFirestore: FirebaseFirestore

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
            sections_action_btn.text = resources.getString(R.string.sectors_action_get)
            firebaseFirestore.runTransaction<Void> { transaction ->
                val documentReferenceUserSnapshot = transaction[userDocument]
                user = documentReferenceUserSnapshot.toObject(User::class.java)!!
                val documentReferencePlanet = firebaseFirestore
                        .collection("Objects").document(user?.locationName!!)
                val documentReferencePlanetMarketSnapshot = transaction[documentReferencePlanet]
                spaceObject = documentReferencePlanetMarketSnapshot.toObject(SpaceObject::class.java)
                null
            }
                    .addOnSuccessListener {
                        sectors_amount.text = getString(R.string.sectors_amount,
                                spaceObject?.availableSectors)
                        if (user?.sectors?.size == 2 || spaceObject?.availableSectors == 0L) {
                            setButtonEnabled(false)
                        } else {
                            setButtonEnabled(true)
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
            if (isEnoughMoney()) {
                activity?.showSnackbarError(getString(R.string.sectors_no_money_to_buy_sectors))
                return@setOnClickListener
            }
            val documentReferencePlanet = firebaseFirestore
                    .collection("Objects").document(user?.locationName!!)
            firebaseFirestore.runTransaction<Void> { transaction ->
                transaction.update(userDocument,
                        "money", user?.money!! -
                        spaceObject?.planetSectorsPrice!!)

                val sector = user?.sectors?.find { it.planetName == user?.locationName }
                if (sector != null) {
                    sector.amount.inc()
                    userDocument.let { transaction.update(it, "sectors", user?.sectors) }
                } else {
                    user?.sectors?.add(Sector(user?.locationName!!, 1))
                    userDocument.let { transaction.update(it, "sectors", user?.sectors) }
                }

                transaction.update(
                        documentReferencePlanet,
                        "availableSectors", spaceObject?.availableSectors!! - 1)
                null
            }
                    .addOnSuccessListener {
                        activity?.showSnackbarError(getString(R.string.sectors_you_bought_sector))
                        sections_action_btn.isEnabled = false
                        context?.let { sections_action_btn.setBackgroundColor(
                                ContextCompat.getColor(it, R.color.grey)) }
                    }
        }
    }

    override fun onDialogCreate() {}

    private fun isEnoughMoney(): Boolean {
        return user?.money!! < spaceObject?.planetSectorsPrice ?: 0
    }

    companion object {
        @JvmStatic
        fun newInstance(): SectorsPlanetFragment {
            val fragment = SectorsPlanetFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}