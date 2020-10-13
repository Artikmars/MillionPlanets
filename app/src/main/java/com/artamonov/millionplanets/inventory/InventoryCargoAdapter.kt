package com.artamonov.millionplanets.inventory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.model.Item
import com.artamonov.millionplanets.utils.getResourceItemName
import kotlinx.android.synthetic.main.inventory_cargo_item.view.*

class InventoryCargoAdapter(
    private val source: InventoryCargoSource,
    itemClickListener: ItemClickListener
) : RecyclerView.Adapter<InventoryCargoAdapter.ViewHolder>() {

    init {
        listener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.inventory_cargo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(source.get(position), source.getItemAmount(position))
    }

    override fun getItemCount(): Int {
        return source.getCount()
    }

    interface ItemClickListener {
        fun onDialogCreate(position: Int)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v

        init {
            v.setOnClickListener(this)
        }

        fun bind(item: Item, amount: Long?) {
            view.inventoryItemName.text = item.itemId?.toInt()?.getResourceItemName()
            view.inventoryItemAmount.text = amount.toString()
        }

        override fun onClick(p0: View?) {
            listener.onDialogCreate(adapterPosition)
        }
    }

    companion object {

        private lateinit var listener: ItemClickListener
    }
}
