package com.milk.open.net.host

import com.milk.open.BuildConfig

interface ApiHost {
    val realUrl: String
        get() = if (BuildConfig.DEBUG) debugUrl() else releaseUrl()

    fun releaseUrl(): String
    fun debugUrl(): String
}