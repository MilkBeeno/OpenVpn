package com.milk.open.net.host

import com.milk.open.BuildConfig

interface ApiDomain {
    val realUrl: String
        get() = if (BuildConfig.DEBUG) debugUrl() else releaseUrl()

    fun releaseUrl(): String
    fun debugUrl(): String
}