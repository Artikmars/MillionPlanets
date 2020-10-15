package com.artamonov.millionplanets.sectors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.User
import kotlinx.android.synthetic.main.market_you_items.view.*
import kotlinx.android.synthetic.main.sectors_you_items.view.*

class SectorsYouAdapter(
    val userList: List<User>,
    private var spaceObjectList: List<SpaceObject>,
    val context: Context,
    val listener: DialogListener
) : RecyclerView.Adapter<SectorsYouAdapter.ViewHolder>() {

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
            itemView.sectors_resource_price.text = (spaceObjectList[adapterPosition].planetSectorsPrice / 2).toString()
            itemView.sectors_resource_amount.text = userList[adapterPosition].sectors.toString()
            itemView.setOnClickListener {
                if (userList[adapterPosition].resource_iron == 0L) {
                    Toast.makeText(context, "No resources to sell", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                listener.onDialogCreate()
            }
        }
    }
}