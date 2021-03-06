package com.artamonov.millionplanets.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.inventory.NumberPickerDialog
import com.artamonov.millionplanets.market.adapter.MarketPlanetAdapter
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.NumberPickerDialogType
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_market_you.*
import javax.inject.Inject

@AndroidEntryPoint
class MarketPlanetFragment : Fragment(), MarketPlanetAdapter.DialogListener,
NumberPickerDialog.NumberPickerDialogListener {
    var firebaseFirestore: FirebaseFirestore? = null
    private var objectModel: SpaceObject? = SpaceObject()
    private var documentReferencePlanet: DocumentReference? = null
    private var user: User? = User()
    private var cargoList: List<Item>? = ArrayList()

    @Inject lateinit var userDocument: DocumentReference

    private fun updateList() {
        userDocument.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                user = doc.toObject(User::class.java)
                cargoList = user?.cargo
                documentReferencePlanet = firebaseFirestore!!.collection("Objects").document(user?.locationName!!)
                documentReferencePlanet!!.get().addOnSuccessListener { doc2 ->
                    if (doc2.exists()) {
                        objectModel = doc2.toObject(SpaceObject::class.java)
                        setAdapter()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseFirestore = FirebaseFirestore.getInstance()
        updateList()
    }

    private fun setAdapter() {
        val marketPlanetAdapter = MarketPlanetAdapter(user, objectModel?.planetClass, this)
        marketPlanetAdapter.notifyDataSetChanged()
        rvMarketYou.adapter = marketPlanetAdapter
        rvMarketYou.layoutManager = LinearLayoutManager(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_market_you, container, false)
    }

    override fun onDialogCreate(position: Int) {
        val fragment = NumberPickerDialog.newInstance(NumberPickerDialogType.MARKET_PLAYER_BUYS,
                10000, position, this)
        fragment.show(fragmentManager)
    }

    companion object {
        @JvmStatic
        fun newInstance(): MarketPlanetFragment {
            val fragment = MarketPlanetFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDismiss() {
        updateList()
    }
}