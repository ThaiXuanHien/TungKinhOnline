package com.hienthai.tungkinhonline

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { AppPrefs(androidContext()) }
}