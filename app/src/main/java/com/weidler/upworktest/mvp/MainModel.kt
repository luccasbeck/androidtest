package com.weidlersoftware.upworktest.mvp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location

import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.weidlersoftware.upworktest.managers.LocationManager
import com.weidlersoftware.upworktest.managers.PermissionManager
import com.weidlersoftware.upworktest.models.AlbumModel
import com.weidlersoftware.upworktest.models.PicturesPicture
import io.realm.Realm
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import io.realm.kotlin.where
import java.lang.Exception
import java.util.*

class MainModel {
    var wifiName: String? = null
    var city: String? = null
    private var context: AppCompatActivity? = null
    private var permissionManager: PermissionManager? = null

    fun setupContext(activity: AppCompatActivity) {
        context = activity
        setupCity()
        setupWifi()
    }

    fun setupWifi() {
        context ?: return
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context!!,
                arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
                203
            )
        } else {

            val wifiMgr = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiMgr.connectionInfo
            val ssid = wifiInfo.ssid
            if(ssid != null && !ssid.contains("unknown ssid")) {
                wifiName = ssid.replace("\"", "")
            }
        }
    }

    fun setupPermissionManager(permissionManager: PermissionManager) {
        this.permissionManager = permissionManager
    }
    fun setupWifiFromNetwork() {
        context ?: return
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context!!,
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                204
            )
        } else {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            if (info != null && info.isConnected) {
                val ssid = info.extraInfo
                if(ssid != null && !ssid.contains("unknown ssid")) {
                    wifiName = ssid.replace("\"", "")
                }
            }
        }
    }

    fun setupCity() {
        if(context == null || permissionManager == null) return
        LocationManager(context!!, permissionManager!!).apply {
            onLocationUpdated {
                if(it.isNullOrEmpty()){ //Testee!
                    val coder = Geocoder(context)
                   coder.getFromLocation(it[0].latitude, it[0].longitude, 1)?.firstOrNull()?.let {adress ->
                      city = adress.locality
                   }

                }
            }
        }
    }
    fun getLastAlbum(): AlbumModel?{
        Realm.getDefaultInstance().use {
            return   it.where<AlbumModel>().sort("lastUpdate").findAll().lastOrNull()
        }
    }

    fun savePhoto(photo: PicturesPicture, album: AlbumModel? = null) {
        Realm.getDefaultInstance().use {
            val lastAlbum = album ?: getLastAlbum()
            if (lastAlbum != null) {
                it.beginTransaction()
                lastAlbum.pictures.add(photo)
                lastAlbum.lastUpdate = Date()
                it.commitTransaction()
            } else {
                it.beginTransaction()
                it.copyToRealm(photo)
                it.commitTransaction()
            }
        }
    }

    fun createAlbum(title: String, parentAlbum: AlbumModel? = null): AlbumModel {
        val albumModel = AlbumModel()
        albumModel.lastUpdate = Date()
        albumModel.name = title
        Realm.getDefaultInstance().use {
            it.beginTransaction()
            try {
                Log.e("test", "try")
                val itm = it.where<AlbumModel>().equalTo("name", title).findFirst()
                if(itm == null)
                parentAlbum?.subAlbums?.add(albumModel) ?: return it.copyToRealm(albumModel)
                else
                    Toast.makeText(context, "Album already exist", Toast.LENGTH_SHORT).show()

            } catch (e: RealmPrimaryKeyConstraintException) {
                Log.e("test", "catch")
                Toast.makeText(context, "Album already exist", Toast.LENGTH_SHORT).show()
            }finally {
                Log.e("test", "finamly")
                it.commitTransaction()
            }

        }
        return albumModel
    }


}