package com.hienthai.tungkinhonline

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.Locale
import kotlin.math.log


class MyApplication : Application() {
    private val prefs: AppPrefs by inject()
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }

        Log.e("Hien", "onCreate: ${Locale.getDefault().language}", )
        LocaleManager.initialize(this, prefs)
        if (Locale.getDefault().language != "vi-VN") {
            LocaleManager.getInstance().setLocale(this, "en-US")
            prefs.language = "en-US"
        }
    }
}