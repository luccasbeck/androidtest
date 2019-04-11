package com.weidlersoftware.upworktest.fragments


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.weidlersoftware.upworktest.AlbumActivity
import com.weidlersoftware.upworktest.DnGActivity
import com.weidlersoftware.upworktest.MainActivity

import com.weidlersoftware.upworktest.R
import com.weidlersoftware.upworktest.adapters.AlbumAdapter
import com.weidlersoftware.upworktest.models.AlbumModel
import io.reactivex.disposables.Disposable
import io.realm.Case
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_blank.*
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.getExternalStoragePublicDirectory
import android.os.Environment
import android.util.Log


class AlbumsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    private val realm = Realm.getDefaultInstance()
    private val albums = realm.where<AlbumModel>().sort("name", Sort.ASCENDING).isEmpty("parent").findAll()
    private val disposables = ArrayList<Disposable>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         val adapter = AlbumAdapter()
        rv_albums.adapter = adapter
        rv_albums.layoutManager = LinearLayoutManager(context)
        adapter.items = albums

        adapter.cameraClick = {item ->
            (activity as? MainActivity)?.getPresenter()?.takePhoto(item)
        }
        adapter.plusClick = {item ->
            (activity as? MainActivity)?.getPresenter()?.addAlbum(item)
        }
        adapter.itemClickListener ={ item ->
            startActivity( Intent(context, AlbumActivity::class.java).apply {
                 putExtra("album", item.name)
             })
        }
        adapter.albumsChoosed = {albums ->
           context?.let {
               AlertDialog.Builder(context!!)
                   .setTitle("Start editing chosed albums?")
                   .setPositiveButton("Yes") { dialog, which ->
                       dialog.dismiss()
                       startEditActivity(albums)
                       adapter.removeSelection()
                   }.setNegativeButton("No") { dialog, which ->
                       dialog.dismiss()
                       adapter.removeSelection()
                   }.show()

           }
        }
        albums.addChangeListener { res ->
            adapter.items = res

        }
       disposables.add ((activity as MainActivity).getPresenter().queryPublisher.distinctUntilChanged()
            .subscribe({query ->
                adapter.items =  albums.where()
                    .sort("name", Sort.ASCENDING)
                    .contains("name", query,Case.INSENSITIVE)
                    .or()
                    .contains("subAlbums.name", query, Case.INSENSITIVE)
                    .findAll()

            }, {
                it.printStackTrace()
            }, {
                adapter.items = albums
                disposables.forEach { it.dispose() }
            }))

        val dcim = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (dcim != null) {

            val dcmAlbum = (activity as MainActivity).getPresenter().model.createAlbum("DCIM")
            val pics = dcim.listFiles()
            if (pics != null) {
                for (pic in pics) {
                    Log.e("pic", pic.absolutePath)
                    if (pic.isDirectory) {
                        (activity as MainActivity).getPresenter().model.createAlbum(pic.name, dcmAlbum)
                    }
                }
            }
        }
    }

    private fun startEditActivity(array: Array<String>){
        Intent(context, DnGActivity::class.java).apply {
            putExtra("albums", array)
            startActivity(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        albums.removeAllChangeListeners()
        realm.close()
    }

}
