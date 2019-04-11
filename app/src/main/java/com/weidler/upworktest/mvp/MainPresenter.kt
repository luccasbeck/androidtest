package com.weidlersoftware.upworktest.mvp

import android.Manifest
import android.app.AlertDialog
import android.app.AppComponentFactory
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.View
import android.widget.EditText
import com.weidlersoftware.upworktest.MainActivity
import com.weidlersoftware.upworktest.managers.TakePhotoManager
import com.weidlersoftware.upworktest.managers.PermissionManager
import com.weidlersoftware.upworktest.models.AlbumModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.*

class MainPresenter(val model: MainModel): SearchView.OnQueryTextListener, View.OnFocusChangeListener  {

    val queryPublisher = PublishSubject.create<String>()
    private val disposables = ArrayList<Disposable>()
    private val photoManager = TakePhotoManager()
    private val permissionManager = PermissionManager()

    private var view: AppCompatActivity? = null
    fun attachView(activity: AppCompatActivity){
        view = activity
        model.setupContext(activity)
        model.setupPermissionManager(permissionManager)
    }

    fun detachView(){
        view = null
    }

    fun viewReady(){
        view?.let { model.setupContext(it) }

    }


    fun takePhoto(album: AlbumModel? = null) {
        view ?: return
        if (ContextCompat.checkSelfPermission(view!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                view!!,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE
                ),
                201
            )
        } else {
            val images = view?.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return
            disposables.add(photoManager.takePicture(view!!, images).subscribe { picture ->
                model.savePhoto(picture, album ?: autoGenerateAlbum())
                disposables.forEach { it.dispose() }
            })

        }
    }

    private fun autoGenerateAlbum(): AlbumModel?{
        val title = model.city ?: model.wifiName ?: return null
       return model.getLastAlbum() ?:  model.createAlbum(title)
    }

    fun addAlbum(album: AlbumModel? = null) {
        view ?: return
        val edtitText = EditText(view)
        val text = model.city ?: model.wifiName
        edtitText.setText(text ?: "")
        AlertDialog.Builder(view!!)
            .setTitle("Album name")
            .setView(edtitText)
            .setPositiveButton("Ok") { dialog, p1 ->
                if (edtitText.text.isNotEmpty()) {
                    model.createAlbum(edtitText.text.toString(), album)
                }
                dialog.dismiss()
            }.setNegativeButton("cancel") { dialog, which ->
                dialog.dismiss()
            }.show()
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        queryPublisher.onNext(query ?: "")
        queryPublisher.onComplete()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        queryPublisher.onNext(newText ?: "")
        return true
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if(hasFocus){
            if (view !is MainActivity)
                (view as MainActivity).changeFragment(1)
        }
    }

    fun onRequestPermissions(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        permissionManager.onPermissionResult(requestCode, permissions, grantResults)

        if (requestCode == 201 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        }

        if (requestCode == 203 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            model.setupWifi()
        }
        if (requestCode == 204 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            model.setupWifiFromNetwork()
        }
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        photoManager.activityResult(requestCode, resultCode, data)

    }
}