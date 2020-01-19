package com.artamonov.millionplanets.market

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.utils.Price.getPlayerSellPrice
import com.artamonov.millionplanets.utils.Utils
import kotlinx.android.synthetic.main.market_you_items.view.*

class MarketYouAdapter(
    private var cargoList: List<Item>,
    private var dialogListener: DialogListener
) : RecyclerView.Adapter<MarketYouAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.market_you_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cargoList[position])
    }

    override fun getItemCount(): Int {
        return cargoList.size
    }

    interface DialogListener {
        fun onDialogCreate(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View) {
//            if (userList.get(adapterPosition).getResource_iron() === 0) {
//                Snackbar.make(view, "No resources to sell", Snackbar.LENGTH_LONG).show()
//                return
//            }
            dialogListener.onDialogCreate(adapterPosition)
        }

        private var view: View = itemView
        fun bind(item: Item) {
            view.resource_name.text = Utils.getResourceItemNameById(item.itemId!!)
            view.resource_price.text = getPlayerSellPrice(item.itemId!!).toString()
            if (item.itemAmount == 0L) {
                view.resource_amount.text = "0"
            } else {
                view.resource_amount.text = item.itemAmount.toString()
            }
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}