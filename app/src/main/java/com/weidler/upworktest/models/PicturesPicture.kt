package com.weidlersoftware.upworktest.models


import android.content.ClipData
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.weidlersoftware.upworktest.R
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.Ignore
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

open class PicturesPicture: RealmObject(), IPictureModel, IItemMdel {



    @PrimaryKey
    var imageUri: String? = null
    var date: Date? = Date()
    @LinkingObjects("pictures")
     val album: RealmResults<AlbumModel>? =null

    fun getDate(): String {
       return SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
    }

    override fun getViewId(): Int {
        return R.layout.pictures_image
    }
    override fun fillItem(v: View) {
        val image = v.findViewById<ImageView>(R.id.iv_picture)
       if(!imageUri.isNullOrBlank()) {
           Glide.with(v).load(imageUri).into(image)
        }
    }
    override fun collSpan(): Int {
        return 1
    }

    override fun getItemName(): String{
       return Uri.parse(imageUri).pathSegments.lastOrNull() ?: ""
    }

    @Ignore
    val longClick = View.OnLongClickListener {
        val data = ClipData.newPlainText("name", this.getItemName())
        val shadowBuilder = View.DragShadowBuilder(it)
        it.tag = this
        it.startDrag(data, shadowBuilder, it, 0)
        true
    }

    override fun fillSubItems(view: View, drag: View.OnDragListener) {
        view.setOnLongClickListener(longClick)
        view.setOnDragListener { v, event ->  false }
        val image: ImageView = view.findViewById(R.id.iv_pic)
        val name: TextView = view.findViewById(R.id.tv_name)
        val subLayout: LinearLayout = view.findViewById(R.id.subitems)
        subLayout.removeAllViews()

        Glide.with(image).load(imageUri).into(image)
        name.text = getItemName()

    }


}