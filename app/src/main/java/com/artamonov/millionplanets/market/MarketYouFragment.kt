package com.artamonov.millionplanets.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.inventory.NumberPickerDialog
import com.artamonov.millionplanets.inventory.NumberPickerDialog.Companion.newInstance
import com.artamonov.millionplanets.market.adapter.MarketYouAdapter
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.model.NumberPickerDialogType
import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_market_you.*
import javax.inject.Inject

@AndroidEntryPoint
class MarketYouFragment : Fragment(), MarketYouAdapter.DialogListener, NumberPickerDialog.NumberPickerDialogListener {
    private var firebaseFirestore: FirebaseFirestore? = null
    private var cargoList: List<Item>? = ArrayList()
    private var user: User? = null
    private var marketYouAdapter: MarketYouAdapter? = null

    @Inject lateinit var userDocument: DocumentReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseFirestore = FirebaseFirestore.getInstance()
        updateList()
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun setAdapter() {
        marketYouAdapter = MarketYouAdapter(cargoList!!, this)
        rvMarketYou.layoutManager = LinearLayoutManager(activity)
        marketYouAdapter?.notifyDataSetChanged()
        rvMarketYou.adapter = marketYouAdapter
    }

    private fun updateList() {
        userDocument.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                user = doc.toObject(User::class.java)
                cargoList = user?.cargo
                setAdapter()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_market_you, container, false)
    }

    override fun onDialogCreate(position: Int) {
        val fragment = newInstance(NumberPickerDialogType.MARKET_PLAYER_SELLS,
                cargoList!![position].itemAmount!!.toInt(), cargoList?.get(position)?.itemId!!.toInt(), this)
        fragment.show(fragmentManager)
    }

    companion object {

        @JvmStatic
        fun newInstance(): MarketYouFragment {
            val fragment = MarketYouFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDismiss() {
        updateList()
    }
}