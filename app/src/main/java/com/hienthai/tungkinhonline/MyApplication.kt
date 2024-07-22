package com.hienthai.tungkinhonline

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class MyApplication : Application() {
    private val prefs: AppPrefs by inject()
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }

        LocaleManager.initialize(this, prefs)
        if (Resources.getSystem().configuration.locales.get(0).language.equals("vi")) {
            LocaleManager.getInstance().setLocale(this, "vi_VN")
            prefs.language = "vi_VN"
        } else {
            LocaleManager.getInstance().setLocale(this, "en_US")
            prefs.language = "en_US"
        }
    }
}