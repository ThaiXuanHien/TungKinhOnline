package com.hienthai.tungkinhonline

import android.content.Context

class AppPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var count: Long by prefs.preferences("count", 0)
}