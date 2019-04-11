package com.weidlersoftware.upworktest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.DragEvent
import android.view.View
import com.weidlersoftware.upworktest.adapters.DragItemsAdapter
import com.weidlersoftware.upworktest.models.AlbumModel
import com.weidlersoftware.upworktest.models.IItemMdel
import com.weidlersoftware.upworktest.models.PicturesPicture
import io.realm.*
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_dn_g.*

class DnGActivity : AppCompatActivity() {
    private val realm = Realm.getDefaultInstance()
    private var albumAquery: AlbumModel? = null
    private var albumBquery: AlbumModel? = null
    private val aAdapter = DragItemsAdapter()
    private val bAdapter = DragItemsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dn_g)
        title = "sortowanie"
        val albums = intent.getStringArrayExtra("albums")
        albumAquery = realm.where<AlbumModel>().equalTo("name", albums[0]).findFirst()
        albumBquery = realm.where<AlbumModel>().equalTo("name", albums[1]).findFirst()

        tv_albuma.text = albumAquery?.name
        tv_albumB.text = albumBquery?.name


        aAdapter.items = transform(albumAquery)
        aAdapter.dragListener = onDragListener
        rv_albuma.layoutManager = LinearLayoutManager(this)
        rv_albuma.adapter = aAdapter
        rv_albuma.setOnDragListener(onDragListener)
        rv_albuma.tag = albumAquery


        bAdapter.dragListener = onDragListener
        bAdapter.items = transform(albumBquery)
        rv_albumb.layoutManager = LinearLayoutManager(this)
        rv_albumb.adapter = bAdapter
        rv_albumb.setOnDragListener(onDragListener)
        rv_albumb.tag = albumBquery
        albumAquery?.addChangeListener<AlbumModel> { album, set ->
            aAdapter.items = transform(album)
        }
        albumBquery?.addChangeListener<AlbumModel> { album, set ->
            bAdapter.items = transform(album)
        }

    }

    private fun transform(albumModel: AlbumModel?): ArrayList<IItemMdel> {
        if (albumModel == null) return ArrayList()
        val res = ArrayList<IItemMdel>()
        res.addAll(albumModel.pictures.map { it as IItemMdel })
        res.addAll(albumModel.subAlbums.map { sa -> sa as IItemMdel })
        res.sortWith(compareBy { itm -> itm.getItemName() })
        return res
    }

    private val onDragListener = View.OnDragListener { v, event ->
        if (event.action == DragEvent.ACTION_DROP) {

            val item = (event.localState as View).tag as? IItemMdel
            if (v.tag == null) return@OnDragListener false
            val toAlbum = (v.tag as AlbumModel)

            val fromAlbum = if (item is AlbumModel) item.parent?.where()?.findFirst()
            else (item as PicturesPicture).album?.first()

           return@OnDragListener swap(fromAlbum, item, toAlbum)
        }


        true
    }

    private fun swap(fromAlbum: AlbumModel?, item: IItemMdel, toAlbum: AlbumModel?): Boolean {

        if (item is PicturesPicture) {
            if (toAlbum?.pictures?.contains(item) == true) return false
            realm.beginTransaction()
            fromAlbum?.pictures?.remove(item)
            toAlbum?.pictures?.add(item)
            realm.commitTransaction()
        }
        if (item is AlbumModel) {
            if (toAlbum?.subAlbums?.contains(item) == true) return false
            if(toAlbum?.parent?.size == 1) return false
            realm.beginTransaction()
            fromAlbum?.subAlbums?.remove(item)
            toAlbum?.subAlbums?.add(item)
            realm.commitTransaction()
        }
        aAdapter.notifyDataSetChanged()
        bAdapter.notifyDataSetChanged()
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
