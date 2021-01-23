package com.artamonov.millionplanets.modules

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.Module
import com.artamonov.millionplanets.model.Weapon
import kotlinx.android.synthetic.main.modules_items.view.*

class ModulesAdapter(
    private val moduleList: List<Module>,
    private val existedItem: List<Weapon>,
    private val context: Context,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<ModulesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.modules_items, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //        if (position == 3 || position == 4) {
        //            holder.modulesPrice.setEnabled(false);
        //            holder.modulesName.setEnabled(false);
        //
        // holder.modulesPrice.setBackgroundColor(context.getResources().getColor(R.color.grey));
        //
        // holder.modulesName.setBackgroundColor(context.getResources().getColor(R.color.grey));
        //        }

        holder.bindItem()
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    interface DialogListener {
        fun onDialogCreate() //  void onDialogSubmit();
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem() {
            itemView.modules_name.text = moduleList[adapterPosition].name
            itemView.modules_price.text = moduleList[adapterPosition].price.toString()
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}