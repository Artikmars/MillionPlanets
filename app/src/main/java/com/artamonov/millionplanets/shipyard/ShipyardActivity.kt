package com.artamonov.millionplanets.shipyard

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.ObjectModel
import com.artamonov.millionplanets.model.User
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.shipyard_activity.*
import java.util.ArrayList

class ShipyardActivity : BaseActivity(R.layout.shipyard_activity), ShipyardAdapter.ItemClickListener {
    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser!!.displayName!!)
        documentReference!!.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList.ship = doc.getString("ship")
                userList.money = doc.getLong("money")!!
                shipyard_user_cash.text = userList.money.toString()
                shipyard_current_ship.text = "Current ship: " + userList.ship

                // For larger amount of spaceships

                /* Map<Integer, Object> shipsMap = new HashMap<>();
                            shipsMap.put(1, shipFighter);
                            shipsMap.put(2, shipTrader);
                            shipsMap.put(3, shipRS);*/

                val shipyardAdapter = ShipyardAdapter(shipsList, this@ShipyardActivity)
                rvShipyard!!.adapter = shipyardAdapter
            }
        }
    }

    internal var userList = User()
    internal var objectModelList = ObjectModel()
    private var figher = User()
    private var trader = User()
    private var rs = User()
    private var shipsList: MutableList<User> = ArrayList()

    private var documentReference: DocumentReference? = null
    private var rvShipyard: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rvShipyard = findViewById(R.id.rvShipyard)
        rvShipyard!!.layoutManager = LinearLayoutManager(this)

        figher.shipPrice = 0
        figher.ship = getString(R.string.fighter)
        shipsList.add(figher)

        trader.shipPrice = 50000
        trader.ship = getString(R.string.trader)
        shipsList.add(trader)

        rs.shipPrice = 100000
        rs.ship = getString(R.string.research_spaceship)
        shipsList.add(rs)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ShipyardInfoActivity::class.java)
        intent.putExtra("position", position)
        intent.putExtra("your_ship", userList.ship)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }

    fun onGoBackToMainOptions(view: View) {
        finish()
    }
}
