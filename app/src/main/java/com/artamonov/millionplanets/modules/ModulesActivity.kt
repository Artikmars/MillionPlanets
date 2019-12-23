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
import com.artamonov.millionplanets.model.Weapon
import com.artamonov.millionplanets.utils.Utils
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.modules.*

class ModulesActivity : BaseActivity(), ModulesAdapter.ItemClickListener {

    internal var documentReference: DocumentReference? = null
    private var modulesRef: DocumentReference? = null
    internal var userList = User()
    private var rvModules: RecyclerView? = null
    private var tvMoney: TextView? = null
    private var modules: MutableList<Module>? = null
    private lateinit var existedItem: List<Weapon>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modules)
        tvMoney = findViewById(R.id.modules_user_cash)

        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)

        rvModules = findViewById(R.id.rvModules)
        rvModules!!.layoutManager = LinearLayoutManager(this)
        modules = Utils.getAllWeapons()
    }

    override fun onStart() {
        super.onStart()
        documentReference = firebaseFirestore.collection("Objects").document(firebaseUser?.displayName!!)
        documentReference?.addSnapshotListener(
                this
        ) { doc, _ ->
            if (doc!!.exists()) {
                userList = doc.toObject(User::class.java)!!
                tvMoney!!.text = userList.money.toString()
                val listOfCurrentWeapons: MutableList<String> = mutableListOf()
                for (weapon in userList.weapon!!.indices) {
                        listOfCurrentWeapons.add(weapon, Utils.getCurrentModuleInfo(userList.weapon!![weapon].weaponId!!)!!.name)
                }
                modules_current_weapons.text = listOfCurrentWeapons.joinToString()
                existedItem = userList.weapon!!
                val modulesAdapter = ModulesAdapter(
                        modules,
                        existedItem,
                        applicationContext,
                        this@ModulesActivity)
                rvModules?.adapter = modulesAdapter
            }
        }
        }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ModulesInfoActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    fun onGoBackToMainOptions(view: View) {
        finish()
    }
}
