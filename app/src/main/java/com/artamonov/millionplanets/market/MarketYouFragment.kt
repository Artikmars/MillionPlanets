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
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_market_you.*

class MarketYouFragment : Fragment(), MarketYouAdapter.DialogListener, NumberPickerDialog.NumberPickerDialogListener {
    private var firebaseFirestore: FirebaseFirestore? = null
    private val userList: List<User>? = null
    private var cargoList: List<Item>? = ArrayList()
    private val objectModelList: List<SpaceObject>? = null
    private var documentReferenceUser: DocumentReference? = null
    private val objectModel: SpaceObject? = null
    private var user: User? = null
    private var marketYouAdapter: MarketYouAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        documentReferenceUser = firebaseFirestore!!.collection("Objects").document(firebaseUser!!.displayName!!)
        updateList()

        //  user = userSnapshot.toObject(User.class);

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
        documentReferenceUser!!.get().addOnSuccessListener { doc ->
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
        //        Button btnAction = view.findViewById(R.id.market_action_btn);
//        btnAction.setVisibility(View.VISIBLE);
//        btnAction.setText(getResources().getString(R.string.sectors_action_sell));
//        btnAction.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        firebaseFirestore.runTransaction(
//                                new Transaction.Function<Void>() {
//                                    @Nullable
//                                    @Override
//                                    public Void apply(@NonNull Transaction transaction)
//                                            throws FirebaseFirestoreException {
//                                      //  transaction.update(documentReferenceInventory, "Iron", 0);
//                                        user.getMoney() = user.getMoney()
//                                                + cargoList.п
//                                                * objectModel.getPrice_buy_iron()
//                                        transaction.update(
//                                                documentReferenceUser,
//                                                "money",
//                                                user.getMoney()
//                                                        + cargoList.п
//                                                                * objectModel.getPrice_buy_iron());
//                                        return null;
//                                    }
//                                });
//                    }
//                });
        return inflater.inflate(R.layout.fragment_market_you, container, false)
    }

    override fun onDialogCreate(position: Int) {
        //        MarketYouDialog marketYouDialog = new MarketYouDialog();
        //        marketYouDialog.show(getFragmentManager(), "text");
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
//        documentReferenceUser!!.get().addOnSuccessListener { doc ->
//            if (doc.exists())  {
//                user  = doc.toObject(User::class.java)
//                cargoList = user?.cargo
//                setAdapter()
//                marketYouAdapter?.notifyDataSetChanged()
//            }
//        }
        updateList()
    }
}