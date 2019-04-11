package com.weidler.upworktest.managers

import com.weidlersoftware.upworktest.models.AlbumModel
import com.weidlersoftware.upworktest.models.PicturesPicture
import io.realm.Realm
import io.realm.kotlin.where
import java.io.File



class TakePhotoManager {
    companion object {
        fun rename(from: File, to: File): Boolean {
            return from.parentFile.exists() && from.exists() && from.renameTo(to)
        }

        fun deleteAllAlbums() {
            val realm = Realm.getDefaultInstance()
            realm.where<AlbumModel>().findAll().forEach { it.deleteFromRealm() }
        }

        fun deleteAllPhotos() {
            val realm = Realm.getDefaultInstance()
            realm.where<PicturesPicture>().findAll().forEach { it.deleteFromRealm() }
        }

    }
}