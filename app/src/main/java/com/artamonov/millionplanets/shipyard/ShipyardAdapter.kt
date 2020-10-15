package com.artamonov.millionplanets.shipyard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.User
import kotlinx.android.synthetic.main.shipyard_items.view.*

class ShipyardAdapter(var shipsList: List<User>, private val listener: ItemClickListener) :
        RecyclerView.Adapter<ShipyardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.shipyard_items, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem()
    }

    override fun getItemCount(): Int {
        return shipsList.size
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    interface DialogListener {
        fun onDialogCreate()
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem() {
            itemView.shipyard_name.text = shipsList[adapterPosition].ship
            itemView.shipyard_price.text = shipsList[adapterPosition].shipPrice.toString()
            itemView.setOnClickListener {
                val position = adapterPosition
                listener.onItemClick(position)
            }
        }
    }
}