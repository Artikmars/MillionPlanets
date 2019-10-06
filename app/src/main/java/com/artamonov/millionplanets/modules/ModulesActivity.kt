package com.artamonov.millionplanets.modules

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.artamonov.millionplanets.model.Module
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Utils
import com.google.firebase.firestore.DocumentReference
import java.util.ArrayList

class ModulesActivity : BaseActivity(), ModulesAdapter.ItemClickListener {
    internal var documentReference: DocumentReference? = null
    private var modulesRef: DocumentReference? = null
    internal var userList = User()
    private var rvModules: RecyclerView? = null
    private var tvMoney: TextView? = null
    private var modules: MutableList<Module>? = null
    private var existedItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modules)
        tvMoney = findViewById(R.id.modules_user_cash)

        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)

        rvModules = findViewById(R.id.rvModules)
        rvModules!!.layoutManager = LinearLayoutManager(this)
        modules = ArrayList()
        modules!!.add(0, Module("Light Laser", 1, 0))
        modules!!.add(1, Module("Medium Laser", 2, 5000))
        modules!!.add(2, Module("Heavy Laser", 3, 10000))
        modules!!.add(3, Module("Military Laser", 2, 0))
        modules!!.add(4, Module("Heavy Military Laser", 3, 0))
    }

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)
        documentReference?.addSnapshotListener(
                this
        ) { doc, e ->
            if (doc!!.exists()) {
                userList.money = doc.getLong("money")!!.toInt()
                tvMoney!!.text = userList.money.toString()
            }
        }
        modulesRef = firebaseFirestore.collection("Modules").document(firebaseUser?.displayName!!)
        modulesRef?.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                // Weapon which is already installed on a s
                existedItem = Utils.getWeaponIdByName(doc.getString("weaponName"))
            }
            val modulesAdapter = ModulesAdapter(
                    modules,
                    existedItem,
                    applicationContext,
                    this@ModulesActivity)
            rvModules?.adapter = modulesAdapter
        }
    }

    override fun onItemClick(position: Int) {

        // Last two weapon types are currently unavailable
        if (position == 3 || position == 4) {
            return
        }
        val intent = Intent(this, ModulesInfoActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    fun onGoBackToMainOptions(view: View) {
        finish()
    }
}
