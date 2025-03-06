package com.hienthai.tungkinhonline

import android.content.Context

class AppPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var count: Long by prefs.preferences("count", 0)
    var remember: Boolean by prefs.preferences("remember", false)
    var id: String by prefs.preferences("id", "")
    var language: String by prefs.preferences("language", "vi_VN")
    var username: String by prefs.preferences("username", "")

    fun clear() {
        prefs.edit()
            .remove("count")
            .remove("remember")
            .remove("id")
            .apply()
    }
}