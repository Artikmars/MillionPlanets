package com.artamonov.millionplanets.inventory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.utils.Utils
import kotlinx.android.synthetic.main.inventory_weapon_item.view.*

class InventoryWeaponAdapter(
    private val source: InventoryWeaponSource,
    itemClickListener: ItemClickListener
) : RecyclerView.Adapter<InventoryWeaponAdapter.ViewHolder>() {

    init {
        listener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.inventory_weapon_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(source.get(position), source.isInstalled(position))
    }

    override fun getItemCount(): Int {
        return source.getCount()
    }

    interface ItemClickListener {
        fun onItemClick(position: Int, isChecked: Boolean)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        fun bind(weaponItem: Long?, isChecked: Boolean) {
//            if (weaponItem == 0L ) {
//                view.relativeLayout.visibility = View.GONE
//                return
//            }
            view.weapon_switch.isChecked = isChecked

            view.weapon_name.text = Utils.getCurrentModuleInfo(weaponItem!!)?.name

            view.weapon_switch.setOnCheckedChangeListener { _, checked
                -> source.setEnabled(adapterPosition, checked) }

//            view.setOnClickListener {
//                source.setEnabled(adapterPosition, isChecked)
//            }
        }
    }

    companion object {

        private lateinit var listener: ItemClickListener
    }
}
