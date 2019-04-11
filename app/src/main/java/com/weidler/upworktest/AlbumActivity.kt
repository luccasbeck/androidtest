package com.weidlersoftware.upworktest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.weidlersoftware.upworktest.adapters.AlbumAdapter
import com.weidlersoftware.upworktest.fragments.PicturesFragment
import com.weidlersoftware.upworktest.models.AlbumModel
import com.weidlersoftware.upworktest.mvp.MainModel
import com.weidlersoftware.upworktest.mvp.MainPresenter
import io.reactivex.disposables.Disposable
import io.realm.Case
import io.realm.Realm
import io.realm.RealmList
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_blank.*

class AlbumActivity : AppCompatActivity() {

    private val albumName by lazy {
        intent?.extras?.getString("album")
    }

    private val model = MainModel()
    private val presenter = MainPresenter(model)

    private val realm = Realm.getDefaultInstance()
    private var sumAlbums = RealmList<AlbumModel>()
    private val disposables = ArrayList<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        title = albumName

        val adapter = AlbumAdapter()
        rv_albums.adapter = adapter
        rv_albums.layoutManager = LinearLayoutManager(this)
        val album = realm.where<AlbumModel>().equalTo("name", albumName).sort("name", Sort.ASCENDING).findFirst()
        album?.let {
            sumAlbums = it.subAlbums
            adapter.items = sumAlbums.toList()
        }

        presenter.attachView(this)
        adapter.cameraClick = {item ->
            presenter.takePhoto(item)
        }

        sumAlbums.addChangeListener { res ->
            adapter.items = res

        }

        disposables.add (presenter.queryPublisher.distinctUntilChanged()
            .subscribe({query ->
                adapter.items = sumAlbums.where()
                    .sort("name", Sort.ASCENDING)
                    .contains("name", query, Case.INSENSITIVE)
                    .or()
                    .contains("subAlbums.name", query, Case.INSENSITIVE)
                    .findAll()

            }, {
                it.printStackTrace()
            }, {
                adapter.items = sumAlbums.toList()
                disposables.forEach { it.dispose() }
            }))

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PicturesFragment.getInstance(albumName))
            .commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        presenter.onRequestPermissions(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

}
