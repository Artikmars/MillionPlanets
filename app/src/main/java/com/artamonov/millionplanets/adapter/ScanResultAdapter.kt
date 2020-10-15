package com.artamonov.millionplanets.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.SpaceObject
import com.artamonov.millionplanets.model.SpaceObjectType
import kotlinx.android.synthetic.main.scan_result_items.view.*

class ScanResultAdapter(
    private var objectList: List<SpaceObject>,
    val itemClickListener: ItemClickListener? = null
) :
        RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.scan_result_items, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem()
    }

    override fun getItemCount(): Int {
        return objectList.size
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem() {
            val spaceObject = objectList[adapterPosition]
            itemView.object_name.text = spaceObject.name
            itemView.distance_to_object.text = spaceObject.distance.toString()
            when (spaceObject.type) {
                SpaceObjectType.PLANET -> {
                    itemView.object_name.setTextColor(Color.parseColor("#FFFF00"))
                    itemView.distance_to_object.setTextColor(Color.parseColor("#FFFF00"))
                }

                SpaceObjectType.FUEL -> {
                    itemView.object_name.setTextColor(Color.parseColor("#008000"))
                    itemView.distance_to_object.setTextColor(Color.parseColor("#008000"))
                }

                else -> {
                    itemView.object_name.setTextColor(Color.parseColor("#ff0000"))
                    itemView.distance_to_object.setTextColor(Color.parseColor("#ff0000"))
                }
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.onItemClick(position)
            }
        }
    }
}