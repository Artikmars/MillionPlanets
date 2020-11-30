package com.artamonov.millionplanets.sectors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import kotlinx.android.synthetic.main.sectors_you_items.view.*

class SectorsPlanetAdapter(
    var userList: MutableList<User>,
    private var spaceObjectList: MutableList<SpaceObject>,
    val listener: DialogListener
) : RecyclerView.Adapter<SectorsPlanetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.sectors_you_items, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem()
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    interface DialogListener {
        fun onDialogCreate()
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem() {
            itemView.sectors_resource_amount.text = spaceObjectList[adapterPosition].availableSectors.toString()
            itemView.sectors_resource_price.text = spaceObjectList[adapterPosition].planetSectorsPrice.toString()
            itemView.setOnClickListener {
                listener.onDialogCreate()
            }
        }
    }
}