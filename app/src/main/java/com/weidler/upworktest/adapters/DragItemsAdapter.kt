package com.weidlersoftware.upworktest.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weidlersoftware.upworktest.R
import com.weidlersoftware.upworktest.models.IItemMdel


class DragItemsAdapter : RecyclerView.Adapter<DragItemsAdapter.DragItemsVH>() {

    var items = ArrayList<IItemMdel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var dragListener: View.OnDragListener

    override fun onCreateViewHolder(parrent: ViewGroup, type: Int): DragItemsVH {
        LayoutInflater.from(parrent.context).inflate(R.layout.drag_item_layout, parrent, false).apply {
            return DragItemsVH(this)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onBindViewHolder(vh: DragItemsVH, pos: Int) {
        val item = items[pos]
        item.fillSubItems(vh.itemView, dragListener)
    }


    class DragItemsVH(itemView: View) : RecyclerView.ViewHolder(itemView)

}