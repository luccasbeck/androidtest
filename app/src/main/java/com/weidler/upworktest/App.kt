package com.weidlersoftware.upworktest

import android.app.Application
import android.content.Context
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import io.realm.Realm
import io.realm.RealmConfiguration
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraMailSender
import org.acra.data.StringFormat

@AcraCore(buildConfigClass = BuildConfig::class,
    reportFormat = StringFormat.JSON)
@AcraMailSender(mailTo = "szymon@weidler.com", reportAsFile = true)
@GlideModule
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("myRealm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.getInstance(config)
        Realm.setDefaultConfiguration(config)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
         ACRA.init(this)
    }
}