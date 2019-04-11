package com.weidlersoftware.upworktest.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weidlersoftware.upworktest.R
import com.weidlersoftware.upworktest.models.AlbumModel


class AlbumAdapter: RecyclerView.Adapter<AlbumAdapter.AlbumVH>() {


    var items: List<AlbumModel> = ArrayList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    private val selectedItems = ArrayList<AlbumModel>()

    var cameraClick: ((item: AlbumModel)-> Unit)? = null
    var plusClick: ((item: AlbumModel)->Unit)? =null
    var itemClickListener: ((item: AlbumModel) -> Unit)? = null

    var albumsChoosed : ((albums: Array<String>)-> Unit)? = null

    fun removeSelection(){
        selectedItems.clear()
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AlbumVH {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.album_layout, p0, false)
        return AlbumVH(view)
    }

    override fun getItemCount() = items.size



    override fun onBindViewHolder(vh: AlbumVH, p1: Int) {
        val item = items[p1]
        item.fillItem( vh.itemView, cameraClick, plusClick, itemClickListener)

        vh.itemView.setOnLongClickListener {
            if(selectedItems.contains(item)){
                selectedItems.remove(item)
            }else{
                if(selectedItems.size >= 2)  return@setOnLongClickListener true
               selectedItems.add(item)
                if(selectedItems.size == 2) albumsChoosed?.invoke(selectedItems.map { it.name }.toTypedArray())
            }
            notifyItemChanged(vh.layoutPosition)
            true
        }
        if(selectedItems.contains(item)){
            vh.itemView.setBackgroundColor(Color.GRAY)
        }else{
            vh.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }


    class AlbumVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}