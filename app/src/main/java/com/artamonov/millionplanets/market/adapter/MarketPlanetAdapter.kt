package com.artamonov.millionplanets.market.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.PlanetType.AnnotationPlanetType
import com.artamonov.millionplanets.model.User
import com.artamonov.millionplanets.utils.Price.getPlayerBuyPrice
import com.artamonov.millionplanets.utils.getResourceItemName
import com.artamonov.millionplanets.utils.getResourceTypeAmountByPlanetType
import kotlinx.android.synthetic.main.market_you_items.view.*

class MarketPlanetAdapter(
    var user: User?,
    @field:AnnotationPlanetType private val planetType: String?,
    private var dialogListener: DialogListener
) : RecyclerView.Adapter<MarketPlanetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.market_you_items, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem()
    }

    override fun getItemCount(): Int {
        return planetType.getResourceTypeAmountByPlanetType()
    }

    interface DialogListener {
        fun onDialogCreate(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem() {
            itemView.resource_name.text = adapterPosition.getResourceItemName()
            itemView.resource_price.text = getPlayerBuyPrice(adapterPosition).toString()
            itemView.setOnClickListener {
                dialogListener.onDialogCreate(adapterPosition)
            }
        }
    }
}