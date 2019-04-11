package com.weidlersoftware.upworktest.fragments


import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.weidlersoftware.upworktest.DetailedActivity
import com.weidlersoftware.upworktest.R
import com.weidlersoftware.upworktest.adapters.PictureAdapter
import com.weidlersoftware.upworktest.models.AlbumModel
import com.weidlersoftware.upworktest.models.IPictureModel
import com.weidlersoftware.upworktest.models.PicturesHeader
import com.weidlersoftware.upworktest.models.PicturesPicture
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_pictures.*

class PicturesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val album = arguments?.getString("albumName")
        result = if (album == null)
            realm.where<PicturesPicture>().findAll()
        else {
            val query = realm.where<PicturesPicture>().equalTo("album.name", album)
            realm.where<AlbumModel>().equalTo("name", album).findFirst()?.let { parent ->
                parent.subAlbums.forEach { subAlbum ->
                    query.or().equalTo("album.name", subAlbum.name)
                }
            }

            // result = realm.where<PicturesPicture>().equalTo("album.name", album).findAll()
            query.findAll()
        }

        return inflater.inflate(R.layout.fragment_pictures, container, false)
    }

    val realm: Realm = Realm.getDefaultInstance()

    private lateinit var result: RealmResults<PicturesPicture>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = GridLayoutManager(context, 6)
        val adapter = PictureAdapter()
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return adapter.items[position].collSpan()
            }
        }
        rv_pictures.layoutManager = layoutManager
        rv_pictures.adapter = adapter
        adapter.items = transform(result)
        adapter.onItemClick =  {item, view ->
            val  options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity as Activity, view, "picture")
            startActivity(Intent(context, DetailedActivity::class.java).apply {
                putExtra("image", item.imageUri)
                putStringArrayListExtra("image_array", getImageUriList(result))
            }, options.toBundle())
        }

        result.addChangeListener { res ->
            adapter.items = transform(res)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun transform(input: List<PicturesPicture>?): List<IPictureModel> {
        input ?: return emptyList()
        val output = ArrayList<IPictureModel>()
        input.groupBy {
            it.getDate()
        }.forEach {
            output.add(PicturesHeader(it.key))
            output.addAll(it.value)
        }
        return output
    }


    private fun getImageUriList(input: List<IPictureModel>?): ArrayList<String> {
        input ?: return ArrayList()

        val output = ArrayList<String>()
        input.forEach {
            if (!(it as PicturesPicture).imageUri.isNullOrEmpty())
                output.add(it.imageUri!!)
        }

        return output
    }

    companion object {

        fun getInstance(albumName: String? = null): PicturesFragment {
            val args = Bundle()
            if (albumName != null)
                args.putString("albumName", albumName)
            val fragment = PicturesFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
