package com.weidlersoftware.upworktest.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weidlersoftware.upworktest.models.IPictureModel
import com.weidlersoftware.upworktest.models.PicturesPicture

class PictureAdapter: RecyclerView.Adapter<PictureAdapter.PicturesVH>() {
    var items: List<IPictureModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClick: ((item: PicturesPicture, view : View) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PicturesVH {
        val view = LayoutInflater.from(p0.context).inflate(p1, p0, false)
        return PicturesVH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(vh: PicturesVH, p1: Int) {
        items[p1].fillItem(vh.itemView)
        vh.itemView.setOnClickListener {
            (items[p1] as? PicturesPicture)?.let {pic ->
                onItemClick?.invoke(pic, it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getViewId()
    }


    class PicturesVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}